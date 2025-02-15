package com.vinaacademy.platform.feature.video.entity;

import java.util.List;

import com.vinaacademy.platform.feature.course.entity.Lesson;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.*;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("VIDEO")
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
