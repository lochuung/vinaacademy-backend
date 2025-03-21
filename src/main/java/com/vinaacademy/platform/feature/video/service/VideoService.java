package com.vinaacademy.platform.feature.video.service;

import com.vinaacademy.platform.feature.video.dto.VideoDto;
import com.vinaacademy.platform.feature.video.dto.VideoRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface VideoService {
    VideoDto uploadVideo(MultipartFile file, VideoRequest videoRequest) throws IOException;
}
