package com.vinaacademy.platform.feature.video.service;

import com.vinaacademy.platform.exception.BadRequestException;
import com.vinaacademy.platform.feature.email.service.EmailService;
import com.vinaacademy.platform.feature.video.entity.Video;
import com.vinaacademy.platform.feature.video.enums.VideoStatus;
import com.vinaacademy.platform.feature.video.properties.VideoProperties;
import com.vinaacademy.platform.feature.video.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VideoProcessorService {
    private final VideoRepository videoRepository;
    private final VideoProperties videoProperties;
    private final EmailService emailService;

    //    @Async("videoTaskExecutor")
    public void convertToHLS(String videoId, Path inputFilePath) {
        Video video = videoRepository.findById  (UUID.fromString(videoId))
                .orElseThrow(() -> BadRequestException.message("Video not found"));
        try {
            // Tạo thư mục output HLS
            Path outputDir = Paths.get(getHlsDir(), videoId);
            if (Files.exists(outputDir)) {
                Files.delete(outputDir);
            }
            Files.createDirectories(outputDir);

            // Đường dẫn file output
            String outputPlaylist = outputDir.resolve("playlist.m3u8")
                    .toString().replace("\\", "/");
            String segmentPattern = outputDir.resolve("segment_%03d.ts")
                    .toString().replace("\\", "/");

            // Lệnh FFmpeg
            ProcessBuilder pb = new ProcessBuilder(
                    "ffmpeg",
                    "-i", inputFilePath.toString(),
                    "-c:v", "libx264",
                    "-b:v", "1500k",
                    "-c:a", "aac",
                    "-b:a", "128k",
                    "-hls_time", "4",
                    "-hls_list_size", "0",
                    "-hls_segment_filename", segmentPattern,
                    "-f", "hls",
                    outputPlaylist
            );

            pb.inheritIO(); // log ra console
            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("✅ Video " + videoId + " converted to HLS successfully.");
                // TODO: Cập nhật status = READY trong DB nếu bạn có DB tracking
                video.setStatus(VideoStatus.READY);

            } else {
                System.err.println("❌ FFmpeg error for videoId: " + videoId);
                // TODO: Cập nhật status = FAILED trong DB
                video.setStatus(VideoStatus.ERROR);
            }

            // Gửi email thông báo
            emailService.sendEmail(video.getAuthor().getEmail(),
                    "Video " + video.getTitle() + " đã được xử lý",
                    "Video " + video.getTitle() + " đã được xử lý xong. Bạn có thể xem video tại đường dẫn: " + video.getVideoUrl(),
                    true);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            // TODO: Cập nhật status = FAILED trong DB
            video.setStatus(VideoStatus.ERROR);
        } finally {
            videoRepository.save(video);
        }
    }

    @Async("videoTaskExecutor")
    public void processVideo(String videoId, MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(getUploadDir(), videoId);
        if (Files.exists(uploadPath)) {
            Files.delete(uploadPath);
        }
        Files.createDirectories(uploadPath);
        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        Path destinationFile = uploadPath.resolve(originalFilename);
        file.transferTo(destinationFile);
        convertToHLS(videoId, destinationFile);
    }


    private String getUploadDir() {
        return videoProperties.getUploadDir();
    }

    private String getHlsDir() {
        return videoProperties.getHlsDir();
    }
}
