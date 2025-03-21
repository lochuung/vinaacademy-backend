package com.vinaacademy.platform.feature.video.entity;

import com.vinaacademy.platform.feature.lesson.entity.Lesson;
import com.vinaacademy.platform.feature.video.enums.VideoStatus;
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
@Table(name = "videos")
public class Video extends Lesson {

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "original_file_name")
    private String originalFilename;

    @Column(name = "hls_path")
    private String hlsPath;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private VideoStatus status;

    @Column(name = "videoUrl")
    private String videoUrl;

    @Column(name = "duration")
    private long duration;

    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VideoNote> videoNotes;
}
