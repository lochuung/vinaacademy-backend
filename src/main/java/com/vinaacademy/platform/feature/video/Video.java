package com.vinaacademy.platform.feature.video;

import java.util.List;

import com.vinaacademy.platform.feature.lesson.Lesson;
import com.vinaacademy.platform.feature.video_note.VideoNote;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
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

    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VideoNote> videoNotes;
}
