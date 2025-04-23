package com.vinaacademy.platform.feature.course;

import com.vinaacademy.platform.exception.BadRequestException;
import com.vinaacademy.platform.feature.common.exception.ResourceNotFoundException;
import com.vinaacademy.platform.feature.common.response.ApiResponse;
import com.vinaacademy.platform.feature.course.dto.CourseDetailsResponse;
import com.vinaacademy.platform.feature.course.dto.CourseDto;
import com.vinaacademy.platform.feature.course.dto.CourseRequest;
import com.vinaacademy.platform.feature.course.dto.CourseSearchRequest;
import com.vinaacademy.platform.feature.course.entity.Course;
import com.vinaacademy.platform.feature.course.repository.CourseRepository;
import com.vinaacademy.platform.feature.course.service.CourseService;
import com.vinaacademy.platform.feature.user.auth.annotation.HasAnyRole;
import com.vinaacademy.platform.feature.user.auth.helpers.SecurityHelper;
import com.vinaacademy.platform.feature.user.constant.AuthConstants;
import com.vinaacademy.platform.feature.user.entity.User;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/courses")
@Slf4j
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class CourseController {
	private final CourseService courseService;
    private final CourseRepository courseRepository;
    private final SecurityHelper securityHelper;
	
    @HasAnyRole({AuthConstants.ADMIN_ROLE, AuthConstants.INSTRUCTOR_ROLE})
    @PostMapping
    public ApiResponse<CourseDto> createCourse(@RequestBody @Valid CourseRequest request) {
        // Only ADMIN and INSTRUCTOR can create courses
        log.debug("Course creating "+request.getName());
        return ApiResponse.success(courseService.createCourse(request));
    }
    
    @GetMapping("/{slug}")
    public ApiResponse<CourseDetailsResponse> getCourseDetails(@PathVariable String slug) {
        log.debug("Getting detailed course information for slug: {}", slug);
        return ApiResponse.success(courseService.getCourse(slug));
    }
    
    //Kiểm tra slug đã tồn tại hay chưa
    @HasAnyRole({AuthConstants.ADMIN_ROLE, AuthConstants.INSTRUCTOR_ROLE})
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

    /**
     * Lấy ID khóa học từ slug
     */
    @GetMapping("/id-by-slug/{slug}")
    public ResponseEntity<ApiResponse<Map<String, UUID>>> getCourseIdBySlug(@PathVariable String slug) {
        try {
            Course course = courseRepository.findBySlug(slug)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học với slug: " + slug));

            Map<String, UUID> response = new HashMap<>();
            response.put("id", course.getId());

            return ResponseEntity.ok(new ApiResponse<>("success", "Lấy ID khóa học thành công", response));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("error", e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("error", "Lỗi khi lấy ID khóa học: " + e.getMessage(), null));
        }
    }

    @GetMapping("/instructor/courses")
    @HasAnyRole({AuthConstants.INSTRUCTOR_ROLE})
    public ApiResponse<Page<CourseDto>> getInstructorCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        User currentUser = securityHelper.getCurrentUser();
        Page<CourseDto> coursePage = courseService.getCoursesByInstructor(
                currentUser.getId(), page, size, sortBy, sortDirection);

        log.debug("Lấy danh sách khóa học của giảng viên: {}", currentUser.getId());
        return ApiResponse.success(coursePage);
    }

    @GetMapping("/instructor/search")
    @HasAnyRole({AuthConstants.INSTRUCTOR_ROLE})
    public ApiResponse<Page<CourseDto>> searchInstructorCourses(
            @ModelAttribute CourseSearchRequest searchRequest,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        User currentUser = securityHelper.getCurrentUser();

        Page<CourseDto> coursePage = courseService.searchInstructorCourses(
                currentUser.getId(), searchRequest, page, size, sortBy, sortDirection);

        log.debug("Tìm kiếm khóa học của giảng viên: {}", currentUser.getId());
        return ApiResponse.success(coursePage);
    }
}
