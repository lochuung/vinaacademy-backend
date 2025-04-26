package com.vinaacademy.platform.feature.video.service;

import com.vinaacademy.platform.exception.BadRequestException;
import com.vinaacademy.platform.feature.email.service.EmailService;
import com.vinaacademy.platform.feature.storage.properties.StorageProperties;
import com.vinaacademy.platform.feature.video.entity.Video;
import com.vinaacademy.platform.feature.video.enums.VideoStatus;
import com.vinaacademy.platform.feature.video.repository.VideoRepository;
import com.vinaacademy.platform.feature.video.utils.FFmpegUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoProcessorService {
    private final VideoRepository videoRepository;
    private final EmailService emailService;
    private final StorageProperties storageProperties;

    @Value("${app.url.frontend}")
    private String frontendUrl;

    @Async("videoTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processVideo(UUID videoId, Path inputFile) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> BadRequestException.message("Video not found"));

        try {
            Path outputDir = Paths.get(storageProperties.getHlsDir(), String.valueOf(videoId));
            Path thumbnailFilePath = Paths.get(storageProperties.getThumbnailDir(), videoId + ".jpg");
            int exitCode = FFmpegUtils.convertToAdaptiveHLS(inputFile, outputDir, thumbnailFilePath);

            if (exitCode == 0) {
                log.info("✅ Video " + videoId + " converted to HLS successfully.");
                video.setStatus(VideoStatus.READY);
                video.setHlsPath(outputDir.toString());
                video.setDuration(FFmpegUtils.getVideoDurationInSeconds(inputFile));
                if (video.getThumbnailUrl() == null) {
                    video.setThumbnailUrl(thumbnailFilePath.toString());
                }

            } else {
                log.error("❌ FFmpeg error for videoId: " + videoId);
                throw new RuntimeException("FFmpeg failed for videoId: " + videoId);
            }

            String courseId = video.getSection().getCourse().getId().toString();

            // Gửi email thông báo
            emailService.sendNotificationEmail(
                    video.getAuthor(),
                    "Video " + video.getTitle() + " đã được xử lý thành công",
                    "Video " + video.getTitle() + " đã được xử lý thành công",
                    // url
                    frontendUrl + "/instructor/courses/" + courseId + "/lectures/" + videoId,
                    // button text
                    "Xem video"
            );
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
}
