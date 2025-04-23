package com.vinaacademy.platform.feature.lesson.service;

import com.vinaacademy.platform.feature.lesson.dto.LessonProgressDto;
import com.vinaacademy.platform.feature.lesson.dto.UserProgressDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface UserProgressService {
    List<LessonProgressDto> getAllLessonProgressByCourse(UUID courseId);
}