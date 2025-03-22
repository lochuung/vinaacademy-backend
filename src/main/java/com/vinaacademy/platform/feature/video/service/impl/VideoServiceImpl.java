package com.vinaacademy.platform.feature.video.service.impl;

import com.vinaacademy.platform.exception.BadRequestException;
import com.vinaacademy.platform.feature.lesson.repository.LessonRepository;
import com.vinaacademy.platform.feature.lesson.repository.projection.LessonAccessInfoDto;
import com.vinaacademy.platform.feature.user.auth.utils.SecurityUtils;
import com.vinaacademy.platform.feature.user.constant.AuthConstants;
import com.vinaacademy.platform.feature.user.entity.User;
import com.vinaacademy.platform.feature.video.mapper.VideoMapper;
import com.vinaacademy.platform.feature.video.dto.VideoDto;
import com.vinaacademy.platform.feature.video.dto.VideoRequest;
import com.vinaacademy.platform.feature.video.entity.Video;
import com.vinaacademy.platform.feature.video.enums.VideoStatus;
import com.vinaacademy.platform.feature.video.properties.VideoProperties;
import com.vinaacademy.platform.feature.video.repository.VideoRepository;
import com.vinaacademy.platform.feature.video.service.VideoProcessorService;
import com.vinaacademy.platform.feature.video.service.VideoService;
import com.vinaacademy.platform.feature.video.validator.VideoValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoServiceImpl implements VideoService {
    private final VideoRepository videoRepository;
    private final VideoProcessorService videoProcessorService;
    private final VideoValidator videoValidator;
    private final VideoMapper videoMapper;
    private final SecurityUtils securityUtils;
    private final LessonRepository lessonRepository;
    private final VideoProperties videoProperties;

    @Override
    public VideoDto uploadVideo(MultipartFile file, VideoRequest videoRequest) throws IOException {
        Video video = videoRepository.findById(videoRequest.getLessonId())
                .orElseThrow(() -> BadRequestException.message("Lesson not found"));
        videoValidator.validate(file);

        User currentUser = securityUtils.getCurrentUser();

        LessonAccessInfoDto lessonAccessInfo = lessonRepository
                .getLessonAccessInfo(videoRequest.getLessonId(),
                        currentUser.getId())
                .orElseThrow(() -> BadRequestException.message("Lesson not found"));
        if (!lessonAccessInfo.isInstructor() && !securityUtils.hasRole(AuthConstants.ADMIN_ROLE)) {
            throw BadRequestException.message("You are not allowed to upload video to this lesson");
        }

        video.setStatus(VideoStatus.PROCESSING);
        video.setOriginalFilename(file.getOriginalFilename());
        video.setDuration(0);
        video.setAuthor(currentUser);

        Path uploadPath = Paths.get(getUploadDir(), video.getId().toString());

        if (Files.exists(uploadPath)) {
            Files.walk(uploadPath)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            log.error(e.getMessage());
                        }
                    });
        }
        Files.createDirectories(uploadPath);
        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        Path destinationFile = uploadPath.resolve(originalFilename);
        file.transferTo(destinationFile);

        video = videoRepository.save(video);

        // Xử lý FFmpeg async
        videoProcessorService.processVideo(video.getId(), destinationFile);


        return videoMapper.toDto(video);
    }

    @Override
    public ResponseEntity<Resource> getVideoSegment(UUID videoId, String subPath) throws MalformedURLException {
        log.debug("Getting video segment: {}/{}", videoId, subPath);
        
        Path segmentPath = Paths.get(videoProperties.getHlsDir(), videoId.toString(), subPath);

        if (!Files.exists(segmentPath)) {
            log.warn("Video segment not found: {}/{}", videoId, subPath);
            return ResponseEntity.notFound().build();
        }

        String filename = segmentPath.getFileName().toString();
        String contentType;
        if (filename.endsWith(".m3u8")) {
            contentType = "application/x-mpegURL";
        } else if (filename.endsWith(".ts")) {
            contentType = "video/MP2T";
        } else {
            contentType = "application/octet-stream";
        }

        Resource resource = new UrlResource(segmentPath.toUri());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .body(resource);
    }

    private String getUploadDir() {
        return videoProperties.getUploadDir();
    }
}
