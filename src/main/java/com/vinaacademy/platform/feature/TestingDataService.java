package com.vinaacademy.platform.feature;

import com.vinaacademy.platform.feature.category.Category;
import com.vinaacademy.platform.feature.category.repository.CategoryRepository;
import com.vinaacademy.platform.feature.user.UserRepository;
import com.vinaacademy.platform.feature.user.constant.AuthConstants;
import com.vinaacademy.platform.feature.user.entity.User;
import com.vinaacademy.platform.feature.user.role.entity.Role;
import com.vinaacademy.platform.feature.user.role.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class TestingDataService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CategoryRepository categoryRepository;

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


}
