package com.vinaacademy.platform.feature.video.dto;

import com.vinaacademy.platform.feature.common.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoDto extends BaseDto {
    @EqualsAndHashCode.Include
    private String videoId;
    private String originalFilename;
    private String status;
    private long duration;
}
