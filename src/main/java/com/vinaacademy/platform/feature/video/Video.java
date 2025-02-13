package com.vinaacademy.platform.feature.video;

import com.vinaacademy.platform.feature.lesson.Lesson;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("VIDEO")
@Getter
@Setter
public class Video extends Lesson {
    @Column(name = "videoUrl")
    private String videoUrl;

    @Column(name = "duration")
    private long duration;

    @Column(name = "transcript")
    private String transcript;
}
