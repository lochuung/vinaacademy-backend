package com.vinaacademy.platform.feature.video;

import com.vinaacademy.platform.exception.UnauthorizedException;
import com.vinaacademy.platform.feature.common.response.ApiResponse;
import com.vinaacademy.platform.feature.lesson.service.LessonService;
import com.vinaacademy.platform.feature.user.auth.annotation.HasAnyRole;
import com.vinaacademy.platform.feature.user.constant.AuthConstants;
import com.vinaacademy.platform.feature.video.dto.VideoDto;
import com.vinaacademy.platform.feature.video.dto.VideoRequest;
import com.vinaacademy.platform.feature.video.properties.VideoProperties;
import com.vinaacademy.platform.feature.video.service.VideoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/videos")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Videos", description = "Video management APIs")
public class VideoController {
    private final VideoService videoService;
    private final LessonService lessonService;
    private final VideoProperties videoProperties;

    @Operation(summary = "Upload a video", description = "Upload a video file for a lesson")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Video uploaded successfully",
                    content = @Content(schema = @Schema(implementation = VideoDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Unauthorized access"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Lesson not found")
    })
    @HasAnyRole({AuthConstants.ADMIN_ROLE, AuthConstants.INSTRUCTOR_ROLE})
    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<VideoDto> uploadVideo(
            @Parameter(description = "Video file")
            @RequestParam("file")
            MultipartFile file,
            @Parameter(description = "Metadata JSON", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = VideoRequest.class)))
            @RequestPart("metadata") @Valid
            VideoRequest videoRequest) throws IOException {
        log.debug("Uploading video for lesson: {}", videoRequest.getLessonId());
        return ApiResponse.success("Video uploaded successfully",
                videoService.uploadVideo(file, videoRequest));
    }

    @Operation(summary = "Get video segment", description = "Get a video segment for streaming")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Video segment retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Video segment not found")
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{videoId}/**")
    public ResponseEntity<Resource> getSegment(HttpServletRequest request,
                                               @PathVariable String videoId) throws MalformedURLException {
        UUID videoUuid = UUID.fromString(videoId);

        // Check user access
        if (!lessonService.hasAccess(videoUuid)) {
            log.warn("Unauthorized access attempt to video: {}", videoId);
            throw UnauthorizedException.message("You don't have access to this video");
        }

        // Extract path after videoId (e.g., 720p/playlist.m3u8)
        String fullPath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String subPath = new AntPathMatcher().extractPathWithinPattern(bestMatchPattern, fullPath);

        // Delegate to service for getting the video segment
        return videoService.getVideoSegment(videoUuid, subPath);
    }
}
