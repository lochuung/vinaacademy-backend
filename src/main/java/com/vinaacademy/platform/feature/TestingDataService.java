package com.vinaacademy.platform.feature;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinaacademy.platform.feature.category.Category;
import com.vinaacademy.platform.feature.category.repository.CategoryRepository;
import com.vinaacademy.platform.feature.common.utils.SlugUtils;
import com.vinaacademy.platform.feature.course.entity.Course;
import com.vinaacademy.platform.feature.course.enums.CourseLevel;
import com.vinaacademy.platform.feature.course.enums.CourseStatus;
import com.vinaacademy.platform.feature.course.repository.CourseRepository;
import com.vinaacademy.platform.feature.instructor.CourseInstructor;
import com.vinaacademy.platform.feature.instructor.repository.CourseInstructorRepository;
import com.vinaacademy.platform.feature.section.entity.Section;
import com.vinaacademy.platform.feature.section.repository.SectionRepository;
import com.vinaacademy.platform.feature.user.UserRepository;
import com.vinaacademy.platform.feature.user.constant.AuthConstants;
import com.vinaacademy.platform.feature.user.entity.User;
import com.vinaacademy.platform.feature.user.role.entity.Role;
import com.vinaacademy.platform.feature.user.role.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestingDataService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CategoryRepository categoryRepository;
    private final CourseRepository courseRepository;
    private final SectionRepository sectionRepository;
    private final CourseInstructorRepository courseInstructorRepository;

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
                .password("admin123")
                .email("locn562836@gmail.com")
                .enabled(true)
                .roles(Set.of(roleRepository.findByCode(AuthConstants.ADMIN_ROLE)))
                .build();
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));

        User staff = User.builder()
                .username("staff")
                .password("staff123")
                .email("huuloc2155@gmail.com")
                .enabled(true)
                .roles(Set.of(roleRepository.findByCode(AuthConstants.STAFF_ROLE)))
                .build();
        staff.setPassword(passwordEncoder.encode(staff.getPassword()));

        User instructor = User.builder()
                .username("instructor")
                .password("instructor123")
                .email("linhpht263@outlook.com.vn")
                .enabled(true)
                .roles(Set.of(roleRepository.findByCode(AuthConstants.INSTRUCTOR_ROLE)))
                .build();
        instructor.setPassword(passwordEncoder.encode(instructor.getPassword()));

        User student = User.builder()
                .username("student")
                .password("student123")
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

    /**
     * Create seed data for categories and courses from JSON file
     * All courses will have empty sections, zero students, and zero ratings
     */
    @Transactional
    public void createSeedDataFromJson() {
        if (courseRepository.count() > 0) {
            log.info("Courses already exist in the database, skipping seed data creation");
            return;
        }

        try {
            // Get instructor user or create one if not exists
            User instructor = userRepository.findByUsername("instructor")
                    .orElseGet(() -> {
                        User newInstructor = User.builder()
                                .username("instructor")
                                .password(passwordEncoder.encode("instructor123"))
                                .email("instructor@example.com")
                                .enabled(true)
                                .roles(Set.of(roleRepository.findByCode(AuthConstants.INSTRUCTOR_ROLE)))
                                .build();
                        return userRepository.save(newInstructor);
                    });

            // Read the JSON data from the file
            ObjectMapper objectMapper = new ObjectMapper();
            ClassPathResource resource = new ClassPathResource("data/categories-courses.json");
            try (InputStream inputStream = resource.getInputStream()) {
                JsonNode rootNode = objectMapper.readTree(inputStream);
                JsonNode categoriesNode = rootNode.get("categories");
                JsonNode coursesNode = rootNode.get("courses");

                // Create categories and build a map for quick lookup
                Map<String, Category> categoryMap = createCategoriesFromJson(categoriesNode);

                // Create courses with empty sections
                createCoursesFromJson(coursesNode, categoryMap, instructor);

                log.info("Successfully created seed data from JSON file");
            }
        } catch (IOException e) {
            log.error("Error reading categories-courses.json", e);
        } catch (Exception e) {
            log.error("Error creating seed data", e);
        }
    }

    /**
     * Create categories from JSON and return a map of id to Category object
     */
    private Map<String, Category> createCategoriesFromJson(JsonNode categoriesNode) {
        Map<String, Category> categoryMap = new HashMap<>();
        Map<String, Category> tempMap = new HashMap<>();
        Set<String> existingSlugs = new HashSet<>(); // Keep track of existing slugs

        // First pass: create all categories without parent relationships
        for (JsonNode categoryNode : categoriesNode) {
            String id = categoryNode.get("id").asText();
            String name = categoryNode.get("name").asText();

            // Generate slug using SlugUtils if needed, or use the one from JSON
            String originalSlug;
            if (categoryNode.has("slug") && !categoryNode.get("slug").isNull()) {
                originalSlug = categoryNode.get("slug").asText();

                if (existingSlugs.contains(originalSlug)) {
                    originalSlug = SlugUtils.toSlug(name);
                }
            } else {
                originalSlug = SlugUtils.toSlug(name);
            }

            // Check for duplicate slug and make it unique if needed
            String slug = ensureUniqueSlug(originalSlug, existingSlugs);

            Category category = Category.builder()
                    .name(name)
                    .slug(slug)
                    .build();

            tempMap.put(id, category);
        }

        // Second pass: set parent relationships and save categories
        for (JsonNode categoryNode : categoriesNode) {
            String id = categoryNode.get("id").asText();
            JsonNode parentIdNode = categoryNode.get("parentId");

            Category category = tempMap.get(id);

            if (parentIdNode != null && !parentIdNode.isNull()) {
                String parentId = parentIdNode.asText();
                Category parentCategory = tempMap.get(parentId);
                category.setParent(parentCategory);
            }

            categoryRepository.save(category);
            categoryMap.put(id, category);
        }

        log.info("Created {} categories from JSON", categoryMap.size());
        return categoryMap;
    }

    /**
     * Helper method to ensure slug uniqueness
     */
    private String ensureUniqueSlug(String originalSlug, Set<String> existingSlugs) {
        String slug = originalSlug;
        int counter = 1;
        // If the slug already exists, append a counter until we have a unique slug
        while (existingSlugs.contains(slug) || categoryRepository.findBySlug(slug).isPresent()) {
            slug = originalSlug + "-" + counter;
            counter++;
        }
        existingSlugs.add(slug);
        return slug;
    }

    /**
     * Create courses with empty sections from JSON
     */
    private void createCoursesFromJson(JsonNode coursesNode, Map<String, Category> categoryMap, User instructor) {
        int count = 0;
        for (JsonNode courseNode : coursesNode) {
            // Skip incomplete course entries
            if (!courseNode.has("id") || !courseNode.has("name") || !courseNode.has("slug") ||
                    !courseNode.has("description") || !courseNode.has("categoryId")) {
                continue;
            }

            try {
                String name = courseNode.get("name").asText();
                String description = courseNode.get("description").asText();
                String slug = courseNode.get("slug").asText();
                String image = courseNode.has("image") ? courseNode.get("image").asText() : "";

                // Get category
                String categoryId = courseNode.get("categoryId").asText();
                Category category = categoryMap.get(categoryId);
                if (category == null) {
                    log.warn("Category with ID {} not found for course {}", categoryId, name);
                    continue;
                }

                // Parse price - default to 0 if not present or invalid
                BigDecimal price = BigDecimal.ZERO;
                if (courseNode.has("price") && !courseNode.get("price").isNull()) {
                    try {
                        price = new BigDecimal(courseNode.get("price").asText());
                    } catch (NumberFormatException e) {
                        log.warn("Invalid price format for course {}: {}", name, e.getMessage());
                    }
                }

                // Parse level - default to BEGINNER
                CourseLevel level = CourseLevel.BEGINNER;
                if (courseNode.has("level") && !courseNode.get("level").isNull()) {
                    try {
                        level = CourseLevel.valueOf(courseNode.get("level").asText());
                    } catch (IllegalArgumentException e) {
                        log.warn("Invalid level for course {}: {}", name, e.getMessage());
                    }
                }

                // Get language - default to Tiếng Việt
                String language = courseNode.has("language") ? courseNode.get("language").asText() : "Tiếng Việt";

                // Create the course with zero students and ratings
                Course course = Course.builder()
                        .name(name)
                        .description(description)
                        .slug(slug)
                        .image(image)
                        .price(price)
                        .level(level)
                        .status(CourseStatus.PUBLISHED)
                        .language(language)
                        .category(category)
                        .rating(0.0)
                        .totalRating(0)
                        .totalStudent(0)
                        .totalSection(2)
                        .totalLesson(0)
                        .sections(new ArrayList<>())
                        .instructors(new ArrayList<>())
                        .build();

                courseRepository.save(course);

                // Assign instructor
                CourseInstructor courseInstructor = CourseInstructor.builder()
                        .instructor(instructor)
                        .course(course)
                        .isOwner(true)
                        .build();

                courseInstructorRepository.save(courseInstructor);

                // Create introduction section
                Section introSection = Section.createSection(
                        null,
                        course,
                        "Giới thiệu khóa học",
                        0,
                        null
                );
                sectionRepository.save(introSection);

                // Create content section
                Section contentSection = Section.createSection(
                        null,
                        course,
                        "Nội dung khóa học",
                        1,
                        null
                );
                sectionRepository.save(contentSection);

                count++;
            } catch (Exception e) {
                log.error("Error creating course: {}", e.getMessage());
            }
        }

        log.info("Created {} courses with empty sections from JSON", count);
    }
}
