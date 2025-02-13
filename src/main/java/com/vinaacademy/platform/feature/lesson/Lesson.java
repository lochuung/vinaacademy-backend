package com.vinaacademy.platform.feature.lesson;

import com.vinaacademy.platform.feature.common.entity.BaseEntity;
import com.vinaacademy.platform.feature.lesson.enums.LessonType;
import com.vinaacademy.platform.feature.section.Section;

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
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // Dùng 1 bảng chung
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

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    protected LessonType type = LessonType.READING;

    @Column(name = "isFree")
    protected boolean isFree = false;

    @Column(name = "orderIndex")
    protected int orderIndex;
}
