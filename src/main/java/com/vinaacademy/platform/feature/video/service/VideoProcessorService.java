package com.vinaacademy.platform.feature.video.service;

import com.vinaacademy.platform.exception.BadRequestException;
import com.vinaacademy.platform.feature.email.service.EmailService;
import com.vinaacademy.platform.feature.video.entity.Video;
import com.vinaacademy.platform.feature.video.enums.VideoStatus;
import com.vinaacademy.platform.feature.video.properties.VideoProperties;
import com.vinaacademy.platform.feature.video.repository.VideoRepository;
import com.vinaacademy.platform.feature.video.utils.FFmpegUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
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
        return FFmpegUtils.convertToAdaptiveHLS(inputFilePath, outputDir);
    }

    @Async("videoTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processVideo(UUID videoId, Path destinationFile) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> BadRequestException.message("Video not found"));
        String videoIdStr = videoId.toString();

        try {
            int exitCode = convertToHLS(videoIdStr, destinationFile);

            if (exitCode == 0) {
                log.info("✅ Video " + videoId + " converted to HLS successfully.");
                video.setStatus(VideoStatus.READY);

            } else {
                log.error("❌ FFmpeg error for videoId: " + videoId);
                throw new RuntimeException("FFmpeg failed for videoId: " + videoId);
            }

            // Gửi email thông báo
            emailService.sendEmail(video.getAuthor().getEmail(),
                    "Video " + video.getTitle() + " đã được xử lý",
                    "Video " + video.getTitle() + " đã được xử lý xong. Bạn có thể xem video tại đường dẫn: "
                            + video.getId(),
                    true);
        } catch (Exception e) {
            log.error(e.getMessage());
            video.setStatus(VideoStatus.ERROR);
            emailService.sendEmail(
                    video.getAuthor().getEmail(),
                    "Lỗi xử lý video " + video.getTitle(),
                    "Có lỗi xảy ra khi xử lý video " + video.getTitle() + ". Vui lòng thử lại sau."
                            + "Chi tiết lỗi: " + e.getMessage(),
                    true
            );
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
