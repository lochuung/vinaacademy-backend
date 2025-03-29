package com.vinaacademy.platform.feature.storage.properties;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class StorageProperties {
    @Value("${application.storage.uploadDir}")
    private String uploadDir;
    @Value("${application.storage.hlsOutputDir}")
    private String hlsDir;
    private String videoDir;
    private String imageDir;
    private String thumbnailDir;
    private String documentDir;

    @PostConstruct
    private void init() {
        videoDir = uploadDir + "/videos";
        imageDir = uploadDir + "/images";
        thumbnailDir = uploadDir + "/thumbnails";
        documentDir = uploadDir + "/documents";
    }
}
