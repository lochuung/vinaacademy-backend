package com.vinaacademy.platform.feature.video.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class VideoProperties {
    @Value("${application.video.uploadDir}")
    private String uploadDir;
    @Value("${application.video.hlsOutputDir}")
    private String hlsDir;
}
