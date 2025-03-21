package com.vinaacademy.platform.feature.video;

import com.vinaacademy.platform.feature.common.response.ApiResponse;
import com.vinaacademy.platform.feature.video.dto.VideoDto;
import com.vinaacademy.platform.feature.video.dto.VideoRequest;
import com.vinaacademy.platform.feature.video.properties.VideoProperties;
import com.vinaacademy.platform.feature.video.service.VideoProcessorService;
import com.vinaacademy.platform.feature.video.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/videos")
@RequiredArgsConstructor
public class VideoController {
    private final VideoService videoService;

    @PostMapping(path = "/upload", consumes = "multipart/form-data")
    public ApiResponse<VideoDto> uploadVideo(@RequestPart("file") MultipartFile file,
                                             @RequestPart("metadata") VideoRequest videoRequest) throws IOException {

        return ApiResponse.success("Video uploaded successfully",
                videoService.uploadVideo(file, videoRequest));

    }

    @GetMapping("/stream/{videoId}")
    public ResponseEntity<?> getStreamingUrl(@PathVariable String videoId) {
        return ResponseEntity.ok(Map.of(
                "hlsUrl", "/" + videoId + "/playlist.m3u8"
        ));
    }

}
