package com.vinaacademy.platform.feature.course;

import com.vinaacademy.platform.feature.common.response.ApiResponse;
import com.vinaacademy.platform.feature.course.dto.CourseDto;
import com.vinaacademy.platform.feature.course.dto.CourseDetailsResponse;
import com.vinaacademy.platform.feature.course.dto.CourseRequest;
import com.vinaacademy.platform.feature.course.dto.CourseSearchRequest;
import com.vinaacademy.platform.feature.course.service.CourseService;
import com.vinaacademy.platform.feature.user.auth.annotation.HasAnyRole;
import com.vinaacademy.platform.feature.user.constant.AuthConstants;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/courses")
@Slf4j
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class CourseController {
	private final CourseService courseService;
	
    @HasAnyRole({AuthConstants.ADMIN_ROLE, AuthConstants.INSTRUCTOR_ROLE})
    @PostMapping
    public ApiResponse<CourseDto> createCourse(@RequestBody @Valid CourseRequest request) {
        // Only ADMIN and INSTRUCTOR can create courses
        log.debug("Course created");
        return ApiResponse.success(courseService.createCourse(request));
    }
    
    @GetMapping("/{slug}")
    public ApiResponse<CourseDetailsResponse> getCourseDetails(@PathVariable String slug) {
        log.debug("Getting detailed course information for slug: {}", slug);
        return ApiResponse.success(courseService.getCourse(slug));
    }
    
    @GetMapping("/check/{slug}")
    public ApiResponse<Boolean> checkCourse(@PathVariable String slug) {
        log.debug("Check course with slug: {}", slug);
        return ApiResponse.success(courseService.existByCourseSlug(slug));
    }
    
    @GetMapping("/pagination")
    public ApiResponse<Page<CourseDto>> getCoursesPaginated(
    		@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(required = false) String categorySlug,
            @RequestParam(defaultValue = "0") double minRating) {
        Page<CourseDto> coursePage = courseService.getCoursesPaginated(
                page, size, sortBy, sortDirection, categorySlug, minRating);
    	log.debug("get list course by sort");
        return ApiResponse.success(coursePage);
    }
    
    @GetMapping("/search")
    public ApiResponse<Page<CourseDto>> searchCourses(
            @Valid @ModelAttribute CourseSearchRequest searchRequest,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        Page<CourseDto> coursePage = courseService.searchCourses(
                searchRequest, page, size, sortBy, sortDirection);
        log.debug("Filter courses with criteria: {}", searchRequest);
        return ApiResponse.success(coursePage);
    }

    @HasAnyRole({AuthConstants.ADMIN_ROLE, AuthConstants.INSTRUCTOR_ROLE})
    @DeleteMapping("/crud/{slug}")
    public ApiResponse<Void> deleteCourse(@PathVariable String slug) {
        // Only ADMIN can delete courses
        log.debug("Course deleted");
        courseService.deleteCourse(slug);
        return ApiResponse.success("Xóa khóa học thành công");
    }

    @HasAnyRole({AuthConstants.INSTRUCTOR_ROLE})
    @PutMapping("/crud/{slug}")
    public ApiResponse<CourseDto> updateCourse(@PathVariable String slug, @RequestBody @Valid CourseRequest request) {
        // Only INSTRUCTOR can update their courses
        log.debug("Course updated");
        return ApiResponse.success(courseService.updateCourse(slug, request));
    }

    @GetMapping("/{slug}/learning")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<CourseDto> getCourseLearning(@PathVariable String slug) {
        log.debug("Getting course learning information for slug: {}", slug);
        return ApiResponse.success(courseService.getCourseLearning(slug));
    }

    @GetMapping("/id/{id}")
    public ApiResponse<CourseDto> getCourseById(@PathVariable UUID id) {
        log.debug("Getting course information for id: {}", id);
        return ApiResponse.success(courseService.getCourseById(id));
    }

    @GetMapping("/slug/{id}")
    public ApiResponse<Map<String, String>> getCourseSlugById(@PathVariable UUID id) {
        log.debug("Getting course slug for id: {}", id);
        String slug = courseService.getCourseSlugById(id);
        Map<String, String> response = new HashMap<>();
        response.put("slug", slug);
        return ApiResponse.success(response);
    }
}
