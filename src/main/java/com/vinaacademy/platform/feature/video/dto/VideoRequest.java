package com.vinaacademy.platform.feature.video.dto;

import com.vinaacademy.platform.feature.common.dto.BaseDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoRequest {
    private UUID lessonId;
}
