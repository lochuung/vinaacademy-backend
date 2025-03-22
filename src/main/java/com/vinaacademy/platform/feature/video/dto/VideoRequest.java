package com.vinaacademy.platform.feature.video.dto;

import com.vinaacademy.platform.feature.common.dto.BaseDto;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoRequest {
    @NotNull(message = "Lesson Id không được để trống")
    private UUID lessonId;
}
