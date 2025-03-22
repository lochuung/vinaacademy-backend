package com.vinaacademy.platform.feature.video.service;

import com.vinaacademy.platform.exception.BadRequestException;
import com.vinaacademy.platform.feature.email.service.EmailService;
import com.vinaacademy.platform.feature.video.entity.Video;
import com.vinaacademy.platform.feature.video.enums.VideoStatus;
import com.vinaacademy.platform.feature.video.properties.VideoProperties;
import com.vinaacademy.platform.feature.video.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoProcessorService {
    private final VideoRepository videoRepository;
    private final VideoProperties videoProperties;
    private final EmailService emailService;

    //    @Async("videoTaskExecutor")
    public int convertToHLS(String videoId, Path inputFilePath) throws InterruptedException, IOException {
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

        return process.waitFor();
    }

    @Async("videoTaskExecutor")
    @Transactional
    public void processVideo(UUID videoId, MultipartFile file) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> BadRequestException.message("Video not found"));
        String videoIdStr = videoId.toString();

        Path uploadPath = Paths.get(getUploadDir(), videoIdStr);

        try {
            if (Files.exists(uploadPath)) {
                Files.delete(uploadPath);
            }
            Files.createDirectories(uploadPath);
            String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            Path destinationFile = uploadPath.resolve(originalFilename);
            file.transferTo(destinationFile);

            int exitCode = convertToHLS(videoIdStr, destinationFile);

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
        } catch (InterruptedException | IOException e) {
            log.error(e.getMessage());
            video.setStatus(VideoStatus.ERROR);
        } finally {
            videoRepository.save(video);
        }
    }


    private String getUploadDir() {
        return videoProperties.getUploadDir();
    }

    private String getHlsDir() {
        return videoProperties.getHlsDir();
    }
}
