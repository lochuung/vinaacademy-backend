package com.vinaacademy.platform.feature.instructor.controller;

import com.vinaacademy.platform.feature.common.response.ApiResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import com.vinaacademy.platform.feature.instructor.dto.InstructorInfoDto;
import com.vinaacademy.platform.feature.instructor.service.InstructorService;

@RestController
@RequestMapping("/api/v1/instructor")
@RequiredArgsConstructor
@Tag(name = "Instructor", description = "Instructor public APIs")
public class InstructorController {

    private final InstructorService instructorService;

    @GetMapping("/{instructorId}")
    public ApiResponse<InstructorInfoDto> getInstructorById(@PathVariable UUID instructorId) {
        InstructorInfoDto instructor = instructorService.getInstructorInfo(instructorId);
        return ApiResponse.success(instructor);
    }
}