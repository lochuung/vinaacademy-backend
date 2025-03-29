package com.vinaacademy.platform.feature.storage.mapper;

import com.vinaacademy.platform.feature.storage.dto.MediaFileDto;
import com.vinaacademy.platform.feature.storage.entity.MediaFile;
import org.mapstruct.Mapper;

@Mapper
public interface MediaFileMapper {
    MediaFileDto toDto(MediaFile mediaFile);
}
