package com.vinaacademy.platform.feature.storage.repository;

import com.vinaacademy.platform.feature.storage.entity.MediaFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MediaFileRepository extends JpaRepository<MediaFile, UUID> {
}
