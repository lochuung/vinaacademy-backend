package com.vinaacademy.platform.feature.course.entity;

import com.vinaacademy.platform.feature.common.entity.BaseEntity;
import com.vinaacademy.platform.feature.course.enums.LessonType;
import com.vinaacademy.platform.feature.video.entity.UserProgress;
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
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "lesson_type", discriminatorType = DiscriminatorType.STRING)
@Table(name = "lessons")
public abstract class Lesson extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @ManyToOne
    @JoinColumn(name = "section_id", nullable = false)
    protected Section section;

    @Column(name = "title")
    protected String title;

    @Column(name = "lesson_type", nullable = false, insertable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    protected LessonType type = LessonType.READING;

    @Column(name = "is_free")
    protected boolean free = false;

    @Column(name = "order_index")
    protected int orderIndex;

    @OneToMany(mappedBy = "lesson", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    protected List<UserProgress> progressList;

}
