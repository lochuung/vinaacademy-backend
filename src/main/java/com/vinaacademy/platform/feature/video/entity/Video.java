package com.vinaacademy.platform.feature.video.entity;

import com.vinaacademy.platform.feature.course.entity.Lesson;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@Getter
@Setter
@SuperBuilder
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
