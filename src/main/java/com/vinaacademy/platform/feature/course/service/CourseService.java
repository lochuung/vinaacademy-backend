package com.vinaacademy.platform.feature.course.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.vinaacademy.platform.feature.course.dto.CourseDto;
import com.vinaacademy.platform.feature.course.dto.CourseRequest;


public interface CourseService {
    List<CourseDto> getCourses();
    
    List<CourseDto> getCoursesByCategory(String slug);
    
    Page<CourseDto> getCoursesPaginated(
            int page,
            int size,
            String sortBy,
            String sortDirection,
            String categorySlug,
            double minRating);

    CourseDto getCourse(String slug);

    CourseDto createCourse(CourseRequest request);

    CourseDto updateCourse(String slug, CourseRequest request);
    
    void deleteCourse(String slug);
}
