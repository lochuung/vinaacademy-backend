package com.vinaacademy.platform.feature.section;

import java.util.List;

import com.vinaacademy.platform.feature.common.entity.BaseEntity;
import com.vinaacademy.platform.feature.course.Course;
import com.vinaacademy.platform.feature.lesson.Lesson;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.CascadeType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Entity
@Table(name = "sections")
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "title")
    private String title;

    @Column(name = "orderIndex")
    private int orderIndex;

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL)
    private List<Lesson> lessons;
}
