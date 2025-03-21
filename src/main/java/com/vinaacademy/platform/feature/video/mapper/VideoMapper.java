package com.vinaacademy.platform.feature.video.mapper;

import com.vinaacademy.platform.feature.video.dto.VideoDto;
import com.vinaacademy.platform.feature.video.entity.Video;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper
public interface VideoMapper {
    @Mapping(target = "videoId", source = "id")
    VideoDto toDto(Video video);

    default String map(UUID value) {
        return value != null ? value.toString() : null;
    }
}
