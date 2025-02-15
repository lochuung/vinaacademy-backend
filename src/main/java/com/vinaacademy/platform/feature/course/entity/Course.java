package com.vinaacademy.platform.feature.course.entity;

import com.vinaacademy.platform.feature.cart.entity.CartItem;
import com.vinaacademy.platform.feature.category.Category;
import com.vinaacademy.platform.feature.common.entity.BaseEntity;
import com.vinaacademy.platform.feature.course.enums.CourseLevel;
import com.vinaacademy.platform.feature.course.enums.CourseStatus;
import com.vinaacademy.platform.feature.enrollment.Enrollment;
import com.vinaacademy.platform.feature.instructor.CourseInstructor;
import com.vinaacademy.platform.feature.order_payment.entity.OrderItem;
import com.vinaacademy.platform.feature.review.CourseReview;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void addSection(Section section) {
        sections.add(section);
        section.setCourse(this);
    }

    public void removeSection(Section section) {
        sections.remove(section);
        section.setCourse(null);
    }

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<Enrollment> enrollments;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseInstructor> instructors = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<CartItem> cartItems;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseReview> courseReviews = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;
}
