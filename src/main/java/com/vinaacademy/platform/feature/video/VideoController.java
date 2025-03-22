package com.vinaacademy.platform.feature.video;

import com.vinaacademy.platform.exception.UnauthorizedException;
import com.vinaacademy.platform.feature.common.response.ApiResponse;
import com.vinaacademy.platform.feature.lesson.service.LessonService;
import com.vinaacademy.platform.feature.video.dto.VideoDto;
import com.vinaacademy.platform.feature.video.dto.VideoRequest;
import com.vinaacademy.platform.feature.video.properties.VideoProperties;
import com.vinaacademy.platform.feature.video.service.VideoProcessorService;
import com.vinaacademy.platform.feature.video.service.VideoService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
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
    private final LessonService lessonService;
    private final VideoProperties videoProperties;

    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<VideoDto> uploadVideo(
            @Parameter(description = "Video file", content = @Content(mediaType = "application/octet-stream"))
            @RequestParam("file")
            MultipartFile file,
            @Parameter(description = "Metadata JSON", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = VideoRequest.class)))
            @RequestPart("metadata") @Valid
            VideoRequest videoRequest) throws IOException {

        return ApiResponse.success("Video uploaded successfully",
                videoService.uploadVideo(file, videoRequest));

    }

    @GetMapping(value = "/{videoId}/{filename:.+}")
    public ResponseEntity<Resource> getSegment(@PathVariable String videoId,
                                               @PathVariable String filename) throws MalformedURLException {
        if (!lessonService.hasAccess(UUID.fromString(videoId))) {
            throw UnauthorizedException.message("You don't have access to this video");
        }

        Path segmentPath = Paths.get(videoProperties.getHlsDir(), videoId, filename);

        if (!Files.exists(segmentPath)) {
            return ResponseEntity.notFound().build();
        }

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
                .body(resource);
    }

}
