package com.vinaacademy.platform.feature.course;

import com.vinaacademy.platform.feature.section.Section;
import com.vinaacademy.platform.feature.user.User;

import java.util.List;
import java.util.Set;

import com.vinaacademy.platform.feature.cart_item.CartItem;
import com.vinaacademy.platform.feature.category.Category;
import com.vinaacademy.platform.feature.common.entity.BaseEntity;
import com.vinaacademy.platform.feature.course.enums.CourseLevel;
import com.vinaacademy.platform.feature.course.enums.CourseStatus;
import com.vinaacademy.platform.feature.course_instructor.CourseInstructor;
import com.vinaacademy.platform.feature.course_review.CourseReview;
import com.vinaacademy.platform.feature.enrollment.Enrollment;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "courses", indexes = {
        @Index(name = "idx_slug", columnList = "slug")
})
public class Course extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "image")
    private String image;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(unique = true, name = "slug")
    private String slug;

    @Column(name = "price")
    private BigDecimal price = BigDecimal.ZERO;

    @Column(name = "level")
    @Enumerated(EnumType.STRING)
    private CourseLevel level = CourseLevel.BEGINNER;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CourseStatus status = CourseStatus.DRAFT;

    @Column(name = "language")
    private String language = "Tiếng Việt";

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "rating")
    private double rating = 0.0;

    @Column(name = "total_rating")
    private long totalRating = 0;

    @Column(name = "total_student")
    private long totalStudent = 0;

    @Column(name = "total_section")
    private long totalSection = 0;

    @Column(name = "total_lesson")
    private long totalLesson = 0;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<Section> sections;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<Enrollment> enrollments;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseInstructor> instructors;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<CartItem> cartItem;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseReview> courseReviews;
}
