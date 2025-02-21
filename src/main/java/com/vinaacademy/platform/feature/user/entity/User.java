package com.vinaacademy.platform.feature.user.entity;

import com.vinaacademy.platform.feature.cart.entity.Cart;
import com.vinaacademy.platform.feature.common.entity.BaseEntity;
import com.vinaacademy.platform.feature.enrollment.Enrollment;
import com.vinaacademy.platform.feature.instructor.CourseInstructor;
import com.vinaacademy.platform.feature.review.CourseReview;
import com.vinaacademy.platform.feature.user.role.entity.Role;
import com.vinaacademy.platform.feature.video.entity.UserProgress;
import com.vinaacademy.platform.feature.video.entity.VideoNote;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "users")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "phone", unique = true)
    private String phone;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "description")
    private String description;

    @Column(name = "is_collaborator")
    private boolean isCollaborator;

    @Column(name = "birthday")
    private LocalDate birthday;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "is_Using_2FA")
    private boolean isUsing2FA = false;

    @Column(name = "failed_attempts")
    @ColumnDefault("0")
    private int failedAttempts;
    @Column(name = "lock_time")
    private LocalDateTime lockTime;

//    @OneToMany(mappedBy = "user")
//    private List<Log> logs;
//
//    @OneToMany(mappedBy = "user")
//    private List<PasswordReset> passwordResets;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VideoNote> videoNotes = new ArrayList<>();

    public void addVideoNote(VideoNote videoNote) {
        videoNotes.add(videoNote);
        videoNote.setUser(this);
    }

    public void removeVideoNote(VideoNote videoNote) {
        videoNotes.remove(videoNote);
        videoNote.setUser(null);
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Enrollment> enrollments = new ArrayList<>();

    public void addEnrollment(Enrollment enrollment) {
        enrollments.add(enrollment);
        enrollment.setUser(this);
    }

    @OneToMany(mappedBy = "instructor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseInstructor> coursesTaught = new ArrayList<>();

    public void addCourseTaught(CourseInstructor courseInstructor) {
        coursesTaught.add(courseInstructor);
        courseInstructor.setInstructor(this);
    }

    public void removeCourseTaught(CourseInstructor courseInstructor) {
        coursesTaught.remove(courseInstructor);
        courseInstructor.setInstructor(null);
    }

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Cart cart;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseReview> courseReviews = new ArrayList<>();

    public void addCourseReview(CourseReview courseReview) {
        courseReviews.add(courseReview);
        courseReview.setUser(this);
    }

    public void removeCourseReview(CourseReview courseReview) {
        courseReviews.remove(courseReview);
        courseReview.setUser(null);
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserProgress> progressList;

}
