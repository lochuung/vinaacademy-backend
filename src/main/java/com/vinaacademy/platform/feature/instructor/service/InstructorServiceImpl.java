package com.vinaacademy.platform.feature.instructor.service;

import com.vinaacademy.platform.exception.BadRequestException;
import com.vinaacademy.platform.feature.instructor.dto.InstructorInfoDto;
import com.vinaacademy.platform.feature.user.UserRepository;
import com.vinaacademy.platform.feature.user.constant.AuthConstants;
import com.vinaacademy.platform.feature.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class InstructorServiceImpl implements InstructorService {
    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public InstructorInfoDto getInstructorInfo(UUID instructorId) {
        User instructor = userRepository.findById(instructorId)
                .orElseThrow(() -> BadRequestException.message("Không tìm thấy giảng viên"));

        // Kiểm tra xem user có role INSTRUCTOR không
        boolean isInstructor = instructor.getRoles().stream()
                .anyMatch(role -> role.getCode().equalsIgnoreCase(AuthConstants.INSTRUCTOR_ROLE));

        if (!isInstructor) {
            throw BadRequestException.message("Người dùng không phải là giảng viên");
        }

        InstructorInfoDto dto = new InstructorInfoDto();
        dto.setFullName(instructor.getFullName());
        dto.setUsername(instructor.getUsername());
        dto.setEmail(instructor.getEmail());
        dto.setDescription(instructor.getDescription());
        dto.setAvatarUrl(instructor.getAvatarUrl());
        return dto;
    }
}