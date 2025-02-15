package com.vinaacademy.platform.feature.course.entity;

import java.util.List;

import com.vinaacademy.platform.feature.common.entity.BaseEntity;
import com.vinaacademy.platform.feature.course.enums.LessonType;
import com.vinaacademy.platform.feature.video.entity.UserProgress;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
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
