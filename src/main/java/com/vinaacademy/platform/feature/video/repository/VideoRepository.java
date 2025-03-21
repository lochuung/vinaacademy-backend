package com.vinaacademy.platform.feature.video.repository;

import com.vinaacademy.platform.feature.video.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VideoRepository extends JpaRepository<Video, UUID> {
    Optional<Video> findById(UUID id);
}
