package com.vinaacademy.platform.feature.course;

import com.vinaacademy.platform.feature.common.response.ApiResponse;
import com.vinaacademy.platform.feature.course.dto.CourseDto;
import com.vinaacademy.platform.feature.course.dto.CourseRequest;
import com.vinaacademy.platform.feature.course.service.CourseService;
import com.vinaacademy.platform.feature.user.auth.annotation.HasAnyRole;
import com.vinaacademy.platform.feature.user.constant.AuthConstants;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    
    @GetMapping
    public ApiResponse<List<CourseDto>> getCourses() {
    	log.debug("get list course");
        return ApiResponse.success(courseService.getCourses());
    }
    
    @GetMapping("/pagination")
    public ApiResponse<List<CourseDto>> getCoursesPaginated(
    		@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(required = false) String categorySlug,
            @RequestParam(defaultValue = "0") double minRating) {
        Page<CourseDto> coursePage = courseService.getCoursesPaginated(
                page, size, sortBy, sortDirection, categorySlug, minRating);
    	log.debug("get list course by sort");
        return ApiResponse.success(coursePage.toList());
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
}
