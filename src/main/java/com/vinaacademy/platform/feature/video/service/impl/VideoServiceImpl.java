package com.vinaacademy.platform.feature.video.service.impl;

import com.vinaacademy.platform.exception.BadRequestException;
import com.vinaacademy.platform.feature.video.mapper.VideoMapper;
import com.vinaacademy.platform.feature.video.dto.VideoDto;
import com.vinaacademy.platform.feature.video.dto.VideoRequest;
import com.vinaacademy.platform.feature.video.entity.Video;
import com.vinaacademy.platform.feature.video.enums.VideoStatus;
import com.vinaacademy.platform.feature.video.repository.VideoRepository;
import com.vinaacademy.platform.feature.video.service.VideoProcessorService;
import com.vinaacademy.platform.feature.video.service.VideoService;
import com.vinaacademy.platform.feature.video.validator.VideoValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {
    private final VideoRepository videoRepository;
    private final VideoProcessorService videoProcessorService;
    private final VideoValidator videoValidator;
    private final VideoMapper videoMapper;

    @Override
    public VideoDto uploadVideo(MultipartFile file, VideoRequest videoRequest) throws IOException {
        Video video = videoRepository.findById(videoRequest.getLessonId())
                .orElseThrow(() -> BadRequestException.message("Lesson not found"));
        videoValidator.validate(file);
        String videoId = UUID.randomUUID().toString();
        video.setStatus(VideoStatus.PROCESSING);
        video.setOriginalFilename(file.getOriginalFilename());
        video.setDuration(0);

        video = videoRepository.save(video);
        // Xử lý FFmpeg async
        videoProcessorService.processVideo(videoId, file);


        return videoMapper.toDto(video);
    }
}
