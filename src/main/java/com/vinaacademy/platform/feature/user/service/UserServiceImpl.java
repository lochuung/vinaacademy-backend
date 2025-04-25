package com.vinaacademy.platform.feature.user.service;

import com.vinaacademy.platform.feature.log.constant.LogConstants;
import com.vinaacademy.platform.feature.user.UserMapper;
import com.vinaacademy.platform.feature.user.UserRepository;
import com.vinaacademy.platform.feature.user.auth.helpers.SecurityHelper;
import com.vinaacademy.platform.feature.user.constant.AuthConstants;
import com.vinaacademy.platform.feature.user.dto.UpdateUserInfoRequest;
import com.vinaacademy.platform.feature.user.dto.UserDto;
import com.vinaacademy.platform.feature.user.entity.User;
import com.vinaacademy.platform.feature.user.role.entity.Role;
import com.vinaacademy.platform.feature.user.role.repository.RoleRepository;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;
    private final SecurityHelper securityHelper;

    @Override
    @Transactional
    public void createTestingData() {
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

    @Override
    public UserDto getCurrentUser() {
        User user = securityHelper.getCurrentUser();
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        return UserMapper.INSTANCE.toDto(user);
    }

    @Override
    @Transactional
    public UserDto updateUserInfo(UpdateUserInfoRequest request) {
        User user = securityHelper.getCurrentUser();

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getDescription() != null) {
            user.setDescription(request.getDescription());
        }
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }
        if (request.getBirthday() != null) {
            user.setBirthday(request.getBirthday());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }

        User savedUser = userRepository.save(user);
        
        return UserMapper.INSTANCE.toDto(savedUser);
    }
}
