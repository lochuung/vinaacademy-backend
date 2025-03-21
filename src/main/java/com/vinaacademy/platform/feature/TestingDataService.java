package com.vinaacademy.platform.feature;

import com.vinaacademy.platform.feature.category.Category;
import com.vinaacademy.platform.feature.category.repository.CategoryRepository;
import com.vinaacademy.platform.feature.course.entity.Course;
import com.vinaacademy.platform.feature.section.entity.Section;
import com.vinaacademy.platform.feature.course.enums.CourseLevel;
import com.vinaacademy.platform.feature.course.enums.CourseStatus;
import com.vinaacademy.platform.feature.course.repository.CourseRepository;
import com.vinaacademy.platform.feature.section.repository.SectionRepository;
import com.vinaacademy.platform.feature.instructor.CourseInstructor;
import com.vinaacademy.platform.feature.instructor.repository.CourseInstructorRepository;
import com.vinaacademy.platform.feature.lesson.dto.LessonRequest;
import com.vinaacademy.platform.feature.lesson.service.LessonService;
import com.vinaacademy.platform.feature.user.UserRepository;
import com.vinaacademy.platform.feature.user.constant.AuthConstants;
import com.vinaacademy.platform.feature.user.entity.User;
import com.vinaacademy.platform.feature.user.role.entity.Role;
import com.vinaacademy.platform.feature.user.role.repository.RoleRepository;
import com.vinaacademy.platform.feature.course.enums.LessonType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TestingDataService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CategoryRepository categoryRepository;
    private final CourseRepository courseRepository;
    private final SectionRepository sectionRepository;
    private final CourseInstructorRepository courseInstructorRepository;
    private final LessonService lessonService;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void createTestingAuthData() {
        String[] roles = {AuthConstants.ADMIN_ROLE, AuthConstants.STAFF_ROLE,
                AuthConstants.INSTRUCTOR_ROLE, AuthConstants.STUDENT_ROLE};
        if (roleRepository.count() > 0) {
            return;
        }
        for (String role : roles) {
            roleRepository.save(Role.builder()
                    .name(role)
                    .code(role).build());
        }

        User admin = User.builder()
                .username("admin")
                .password("admin")
                .email("locn562836@gmail.com")
                .enabled(true)
                .roles(Set.of(roleRepository.findByCode(AuthConstants.ADMIN_ROLE)))
                .build();
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));

        User staff = User.builder()
                .username("staff")
                .password("staff")
                .email("huuloc2155@gmail.com")
                .enabled(true)
                .roles(Set.of(roleRepository.findByCode(AuthConstants.STAFF_ROLE)))
                .build();
        staff.setPassword(passwordEncoder.encode(staff.getPassword()));

        User instructor = User.builder()
                .username("instructor")
                .password("instructor")
                .email("linhpht263@outlook.com.vn")
                .enabled(true)
                .roles(Set.of(roleRepository.findByCode(AuthConstants.INSTRUCTOR_ROLE)))
                .build();
        instructor.setPassword(passwordEncoder.encode(instructor.getPassword()));

        User student = User.builder()
                .username("student")
                .password("student")
                .email("trihung987@gmail.com")
                .enabled(true)
                .roles(Set.of(roleRepository.findByCode(AuthConstants.STUDENT_ROLE)))
                .build();
        student.setPassword(passwordEncoder.encode(student.getPassword()));

        userRepository.save(admin);
        userRepository.save(staff);
        userRepository.save(instructor);
        userRepository.save(student);
    }

    @Transactional
    public void createTestingCategoryData() {
        if (categoryRepository.count() > 0) {
            return;
        }

        // Create root categories
        Category root1 = Category.builder()
                .name("IT")
                .slug("it")
                .build();

        Category root2 = Category.builder()
                .name("Marketing")
                .slug("marketing")
                .build();

        Category root3 = Category.builder()
                .name("Design")
                .slug("design")
                .build();

        categoryRepository.save(root1);
        categoryRepository.save(root2);
        categoryRepository.save(root3);

        // Create sub categories
        Category sub1 = Category.builder()
                .name("Java")
                .slug("java")
                .parent(root1)
                .build();

        Category sub2 = Category.builder()
                .name("Python")
                .slug("python")
                .parent(root1)
                .build();

        Category sub3 = Category.builder()
                .name("Digital Marketing")
                .slug("digital-marketing")
                .parent(root2)
                .build();

        Category sub4 = Category.builder()
                .name("Graphic Design")
                .slug("graphic-design")
                .parent(root3)
                .build();

        categoryRepository.save(sub1);
        categoryRepository.save(sub2);
        categoryRepository.save(sub3);
        categoryRepository.save(sub4);

        // Create sub sub categories
        Category subSub1 = Category.builder()
                .name("Spring Boot")
                .slug("spring-boot")
                .parent(sub1)
                .build();

        Category subSub2 = Category.builder()
                .name("Spring Cloud")
                .slug("spring-cloud")
                .parent(sub1)
                .build();

        Category subSub3 = Category.builder()
                .name("Django")
                .slug("django")
                .parent(sub2)
                .build();

        Category subSub4 = Category.builder()
                .name("Flask")
                .slug("flask")
                .parent(sub2)
                .build();

        categoryRepository.save(subSub1);
        categoryRepository.save(subSub2);
        categoryRepository.save(subSub3);
        categoryRepository.save(subSub4);

    }

    @Transactional
    public void createTestingCourseData() {
        if (courseRepository.count() > 0) {
            return;
        }

        // Get instructor user
        User instructor = userRepository.findByUsername("instructor")
                .orElseThrow(() -> new RuntimeException("Instructor user not found"));
        
        // Get categories
        Category javaCategory = categoryRepository.findBySlug("java")
                .orElseThrow(() -> new RuntimeException("Java category not found"));
        
        Category pythonCategory = categoryRepository.findBySlug("python")
                .orElseThrow(() -> new RuntimeException("Python category not found"));
        
        Category springBootCategory = categoryRepository.findBySlug("spring-boot")
                .orElseThrow(() -> new RuntimeException("Spring Boot category not found"));
        
        // Create Java course
        Course javaCourse = Course.builder()
                .name("Java Programming Fundamentals")
                .description("Learn the basics of Java programming language")
                .slug("java-programming-fundamentals")
                .price(new BigDecimal("299000"))
                .level(CourseLevel.BEGINNER)
                .status(CourseStatus.PUBLISHED)
                .language("Tiếng Việt")
                .category(javaCategory)
                .build();
        
        courseRepository.save(javaCourse);
        
        // Assign instructor to course
        CourseInstructor courseInstructor = CourseInstructor.builder()
                .instructor(instructor)
                .course(javaCourse)
                .isOwner(true)
                .build();
        
        courseInstructorRepository.save(courseInstructor);
        
        // Create sections for Java course
        Section section1 = Section.createSection(
                null, 
                javaCourse,
                "Introduction to Java",
                0,
                null
        );
        
        Section section2 = Section.createSection(
                null, 
                javaCourse,
                "Java Basics",
                1,
                null
        );
        
        // Add a section for video content
        Section section3 = Section.createSection(
                null,
                javaCourse,
                "Java Videos",
                2,
                null
        );
        
        sectionRepository.save(section1);
        sectionRepository.save(section2);
        sectionRepository.save(section3);
        
        // Create lessons for Section 1
        LessonRequest lesson1 = LessonRequest.builder()
                .title("What is Java?")
                .type(LessonType.READING)
                .content("<p>Java is a high-level, class-based, object-oriented programming language...</p>")
                .sectionId(section1.getId())
                .free(true)
                .orderIndex(0)
                .build();
        
        LessonRequest lesson2 = LessonRequest.builder()
                .title("Setting up Java Development Environment")
                .type(LessonType.READING)
                .content("<p>In this lesson, we'll learn how to set up Java on your computer...</p>")
                .sectionId(section1.getId())
                .free(false)
                .orderIndex(1)
                .build();
        
        LessonRequest lesson3 = LessonRequest.builder()
                .title("Java Quiz")
                .type(LessonType.QUIZ)
                .sectionId(section1.getId())
                .free(false)
                .orderIndex(2)
                .passPoint(7.0)
                .totalPoint(10.0)
                .duration(30)
                .build();
        
        // Create lessons for Section 2
        LessonRequest lesson4 = LessonRequest.builder()
                .title("Variables and Data Types")
                .type(LessonType.READING)
                .content("<p>In this lesson, we'll learn about variables and data types in Java...</p>")
                .sectionId(section2.getId())
                .free(false)
                .orderIndex(0)
                .build();
        
        LessonRequest lesson5 = LessonRequest.builder()
                .title("Control Flow Statements")
                .type(LessonType.READING)
                .content("<p>In this lesson, we'll learn about control flow statements in Java...</p>")
                .sectionId(section2.getId())
                .free(false)
                .orderIndex(1)
                .build();
        
        // Create video lessons for Section 3
        LessonRequest videoLesson1 = LessonRequest.builder()
                .title("Java Introduction Video")
                .type(LessonType.VIDEO)
                .sectionId(section3.getId())
                .free(true)
                .orderIndex(0)
                .thumbnailUrl("https://example.com/thumbnails/java-intro.jpg")
                .build();
        
        LessonRequest videoLesson2 = LessonRequest.builder()
                .title("Setting Up Java Environment - Video Tutorial")
                .type(LessonType.VIDEO)
                .sectionId(section3.getId())
                .free(false)
                .orderIndex(1)
                .thumbnailUrl("https://example.com/thumbnails/java-setup.jpg")
                .build();
        
        LessonRequest videoLesson3 = LessonRequest.builder()
                .title("Object-Oriented Programming in Java")
                .type(LessonType.VIDEO)
                .sectionId(section3.getId())
                .free(false)
                .orderIndex(2)
                .thumbnailUrl("https://example.com/thumbnails/java-oop.jpg")
                .build();
        
        // Create lessons with instructor as explicit author
        lessonService.createLesson(lesson1, instructor);
        lessonService.createLesson(lesson2, instructor);
        lessonService.createLesson(lesson3, instructor);
        lessonService.createLesson(lesson4, instructor);
        lessonService.createLesson(lesson5, instructor);
        
        // Create video lessons
        lessonService.createLesson(videoLesson1, instructor);
        lessonService.createLesson(videoLesson2, instructor);
        lessonService.createLesson(videoLesson3, instructor);
        
        // Create Spring Boot course
        Course springBootCourse = Course.builder()
                .name("Spring Boot for Beginners")
                .description("Learn Spring Boot framework for Java applications")
                .slug("spring-boot-for-beginners")
                .price(new BigDecimal("499000"))
                .level(CourseLevel.INTERMEDIATE)
                .status(CourseStatus.PUBLISHED)
                .language("Tiếng Việt")
                .category(springBootCategory)
                .build();
        
        courseRepository.save(springBootCourse);
        
        // Create a section for Spring Boot videos
        Section springBootVideosSection = Section.createSection(
                null,
                springBootCourse,
                "Spring Boot Video Tutorials",
                0,
                null
        );
        
        sectionRepository.save(springBootVideosSection);
        
        // Add video lessons to Spring Boot course
        LessonRequest springBootVideo1 = LessonRequest.builder()
                .title("Introduction to Spring Boot")
                .type(LessonType.VIDEO)
                .sectionId(springBootVideosSection.getId())
                .free(true)
                .orderIndex(0)
                .thumbnailUrl("https://example.com/thumbnails/spring-boot-intro.jpg")
                .build();
        
        LessonRequest springBootVideo2 = LessonRequest.builder()
                .title("Creating Your First Spring Boot Application")
                .type(LessonType.VIDEO)
                .sectionId(springBootVideosSection.getId())
                .free(false)
                .orderIndex(1)
                .thumbnailUrl("https://example.com/thumbnails/spring-boot-first-app.jpg")
                .build();
        
        // Create Spring Boot video lessons
        lessonService.createLesson(springBootVideo1, instructor);
        lessonService.createLesson(springBootVideo2, instructor);
        
        // Assign instructor to Spring Boot course
        CourseInstructor courseInstructor2 = CourseInstructor.builder()
                .instructor(instructor)
                .course(springBootCourse)
                .isOwner(true)
                .build();
        
        courseInstructorRepository.save(courseInstructor2);
        
        // Create Python course
        Course pythonCourse = Course.builder()
                .name("Python for Data Science")
                .description("Learn Python programming for data analysis and visualization")
                .slug("python-for-data-science")
                .price(new BigDecimal("399000"))
                .level(CourseLevel.BEGINNER)
                .status(CourseStatus.PUBLISHED)
                .language("Tiếng Việt")
                .category(pythonCategory)
                .build();
        
        courseRepository.save(pythonCourse);
        
        // Assign instructor to Python course
        CourseInstructor courseInstructor3 = CourseInstructor.builder()
                .instructor(instructor)
                .course(pythonCourse)
                .isOwner(true)
                .build();
        
        courseInstructorRepository.save(courseInstructor3);
    }
}
