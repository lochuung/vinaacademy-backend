package com.vinaacademy.platform.feature.user;

import java.time.LocalDate;
import java.util.UUID;
import java.util.List;
import java.util.Set;

import com.vinaacademy.platform.feature.cart.Cart;
import com.vinaacademy.platform.feature.common.entity.BaseEntity;
import com.vinaacademy.platform.feature.course.Course;
import com.vinaacademy.platform.feature.course_instructor.CourseInstructor;
import com.vinaacademy.platform.feature.course_review.CourseReview;
import com.vinaacademy.platform.feature.enrollment.Enrollment;
import com.vinaacademy.platform.feature.log.Log;
import com.vinaacademy.platform.feature.password_reset.PasswordReset;
import com.vinaacademy.platform.feature.role.Role;
import com.vinaacademy.platform.feature.user_progress.UserProgress;
import com.vinaacademy.platform.feature.video_note.VideoNote;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.GenerationType;
import jakarta.persistence.CascadeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "users")

public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "email")
    private String email;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "phone")
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

    @ManyToMany
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "is_Using_2FA")
    private boolean isUsing2FA;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true) // If a Log is deleted from the log
                                                                                   // list, it is also deleted from the
                                                                                   // database.
    private List<Log> logs;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true) // If a PasswordReset is deleted from
                                                                                   // the passwordReset list, it is also
                                                                                   // deleted from the database.
    private List<PasswordReset> passwordResets;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true) // If a VideoNote is deleted from the
                                                                                   // videoNote list, it is also deleted
                                                                                   // from the database.
    private List<VideoNote> videoNotes;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true) // If a Course is deleted from the
                                                                                   // course list, it is also deleted
                                                                                   // from
                                                                                   // the database.
    private List<Enrollment> enrollments;

    @OneToMany(mappedBy = "instructor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseInstructor> coursesTaught;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Cart cart;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseReview> courseReviews;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserProgress> progressList;

}
