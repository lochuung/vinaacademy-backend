package com.vinaacademy.platform.feature.storage.service;

import com.vinaacademy.platform.feature.storage.dto.MediaFileDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface StorageService {
    MediaFileDto uploadFile(MultipartFile file, String userId) throws IOException;

    MediaFileDto loadFile(UUID id);

    MediaFileDto getMediaFileById(UUID id);
}
