package com.vinaacademy.platform.feature.enrollment.controller;

import com.vinaacademy.platform.feature.common.exception.ResourceNotFoundException;
import com.vinaacademy.platform.feature.common.response.ApiResponse;
import com.vinaacademy.platform.feature.common.response.PaginationResponse;
import com.vinaacademy.platform.feature.enrollment.dto.EnrollmentRequest;
import com.vinaacademy.platform.feature.enrollment.dto.EnrollmentResponse;
import com.vinaacademy.platform.feature.enrollment.enums.ProgressStatus;
import com.vinaacademy.platform.feature.enrollment.service.EnrollmentService;
import com.vinaacademy.platform.feature.user.UserRepository;
import com.vinaacademy.platform.feature.user.auth.annotation.HasAnyRole;
import com.vinaacademy.platform.feature.user.auth.helpers.SecurityHelper;
import com.vinaacademy.platform.feature.user.constant.AuthConstants;
import com.vinaacademy.platform.feature.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/enrollments")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Enrollment", description = "Quản lý đăng ký khóa học")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final UserRepository userRepository;
    private final SecurityHelper securityHelper;

    /**
     * Đăng ký khóa học mới
     */
    @HasAnyRole({AuthConstants.STUDENT_ROLE})
    @PostMapping
    @Operation(summary = "Đăng ký khóa học mới", description = "Học viên đăng ký khóa học mới")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> enrollCourse(
            @Valid @RequestBody EnrollmentRequest request) {

        UUID userId = securityHelper.getCurrentUser().getId();

        EnrollmentResponse enrollmentResponse = enrollmentService.enrollCourse(request, userId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<EnrollmentResponse>builder()
                        .status("success")
                        .message("Đăng ký khóa học thành công")
                        .data(enrollmentResponse)
                        .build());
    }

    /**
     * Kiểm tra đã đăng ký khóa học chưa
     */
    @HasAnyRole({AuthConstants.STUDENT_ROLE})
    @GetMapping("/check")
    @Operation(summary = "Kiểm tra đăng ký", description = "Kiểm tra xem học viên đã đăng ký khóa học chưa")
    public ResponseEntity<ApiResponse<Boolean>> checkEnrollment(
            @RequestParam UUID courseId) {
        UUID userId = securityHelper.getCurrentUser().getId();
        boolean isEnrolled = enrollmentService.isEnrolled(userId, courseId);

        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .status("success")
                .data(isEnrolled)
                .build());
    }

    /**
     * Lấy danh sách khóa học đã đăng ký
     */
    @HasAnyRole({AuthConstants.STUDENT_ROLE})
    @GetMapping
    @Operation(summary = "Danh sách khóa học đã đăng ký", description = "Lấy danh sách các khóa học mà học viên đã đăng ký")
    public ResponseEntity<ApiResponse<PaginationResponse<EnrollmentResponse>>> getUserEnrollments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) ProgressStatus status) {

        UUID userId = securityHelper.getCurrentUser().getId();

        Pageable pageable = PageRequest.of(page, size, Sort.by("startAt").descending());

        if (status != null) {
            List<EnrollmentResponse> enrollments = enrollmentService.getUserEnrollmentsByStatus(userId, status);
            return ResponseEntity.ok(ApiResponse.<PaginationResponse<EnrollmentResponse>>builder()
                    .status("success")
                    .data(PaginationResponse.<EnrollmentResponse>builder()
                            .content(enrollments)
                            .totalElements((long) enrollments.size())
                            .totalPages(1)
                            .currentPage(0)
                            .size(enrollments.size())
                            .build())
                    .build());
        } else {
            Page<EnrollmentResponse> enrollmentsPage = enrollmentService.getUserEnrollments(userId, pageable);
            return ResponseEntity.ok(ApiResponse.<PaginationResponse<EnrollmentResponse>>builder()
                    .status("success")
                    .data(PaginationResponse.<EnrollmentResponse>builder()
                            .content(enrollmentsPage.getContent())
                            .totalElements(enrollmentsPage.getTotalElements())
                            .totalPages(enrollmentsPage.getTotalPages())
                            .currentPage(enrollmentsPage.getNumber())
                            .size(enrollmentsPage.getSize())
                            .build())
                    .build());
        }
    }

    /**
     * Lấy thông tin chi tiết về đăng ký
     */
    @HasAnyRole({AuthConstants.STUDENT_ROLE, AuthConstants.INSTRUCTOR_ROLE, AuthConstants.ADMIN_ROLE})
    @GetMapping("/{enrollmentId}")
    @Operation(summary = "Chi tiết đăng ký", description = "Xem thông tin chi tiết về đăng ký khóa học")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> getEnrollment(
            @PathVariable Long enrollmentId) {

        Authentication authentication = securityHelper.getAuthentication();

        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.<EnrollmentResponse>builder()
                            .status("error")
                            .message("Người dùng chưa xác thực")
                            .build());
        }

        EnrollmentResponse enrollmentResponse = enrollmentService.getEnrollment(enrollmentId);

        return ResponseEntity.ok(ApiResponse.<EnrollmentResponse>builder()
                .status("success")
                .data(enrollmentResponse)
                .build());
    }

    /**
     * Cập nhật tiến độ học tập
     */
    @HasAnyRole({AuthConstants.STUDENT_ROLE})
    @PatchMapping("/{enrollmentId}/progress")
    @Operation(summary = "Cập nhật tiến độ", description = "Cập nhật tiến độ học tập của khóa học")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> updateProgress(
            @PathVariable Long enrollmentId,
            @RequestParam Double progressPercentage) {

        User user = securityHelper.getCurrentUser();

        // Kiểm tra xem người dùng có quyền cập nhật tiến độ này không
        // Logic kiểm tra quyền có thể thêm vào đây hoặc trong Service layer

        EnrollmentResponse updatedEnrollment = enrollmentService.updateProgress(enrollmentId, progressPercentage);

        return ResponseEntity.ok(ApiResponse.<EnrollmentResponse>builder()
                .status("success")
                .message("Cập nhật tiến độ thành công")
                .data(updatedEnrollment)
                .build());
    }

    /**
     * Cập nhật trạng thái học tập
     */
    @HasAnyRole({AuthConstants.STUDENT_ROLE, AuthConstants.INSTRUCTOR_ROLE, AuthConstants.ADMIN_ROLE})
    @PatchMapping("/{enrollmentId}/status")
    @Operation(summary = "Cập nhật trạng thái", description = "Cập nhật trạng thái học tập (đang học, hoàn thành, dừng học)")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> updateStatus(
            @PathVariable Long enrollmentId,
            @RequestParam ProgressStatus status) {


        User user = securityHelper.getCurrentUser();

        EnrollmentResponse updatedEnrollment = enrollmentService.updateStatus(enrollmentId, status);

        return ResponseEntity.ok(ApiResponse.<EnrollmentResponse>builder()
                .status("success")
                .message("Cập nhật trạng thái thành công")
                .data(updatedEnrollment)
                .build());
    }

    /**
     * Hủy đăng ký khóa học
     */
    @HasAnyRole({AuthConstants.STUDENT_ROLE, AuthConstants.ADMIN_ROLE})
    @DeleteMapping("/{enrollmentId}")
    @Operation(summary = "Hủy đăng ký khóa học", description = "Học viên hủy đăng ký khóa học")
    public ResponseEntity<ApiResponse<Void>> cancelEnrollment(
            @PathVariable Long enrollmentId) {

        User user = securityHelper.getCurrentUser();

        // Có thể thêm logic kiểm tra quyền hủy đăng ký tại đây hoặc trong Service layer

        enrollmentService.cancelEnrollment(enrollmentId);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status("success")
                .message("Hủy đăng ký khóa học thành công")
                .build());
    }

    /**
     * Lấy danh sách học viên của một khóa học (dành cho giảng viên)
     */
    @HasAnyRole({AuthConstants.INSTRUCTOR_ROLE, AuthConstants.ADMIN_ROLE})
    @GetMapping("/course/{courseId}")
    @Operation(summary = "Danh sách học viên", description = "Lấy danh sách học viên đã đăng ký một khóa học")
    public ResponseEntity<ApiResponse<PaginationResponse<EnrollmentResponse>>> getCourseEnrollments(
            @PathVariable UUID courseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) ProgressStatus status) {

        User user = securityHelper.getCurrentUser();

        // Có thể thêm logic kiểm tra quyền truy cập khóa học tại đây

        Pageable pageable = PageRequest.of(page, size);
        Page<EnrollmentResponse> enrollmentsPage = enrollmentService.getCourseEnrollments(courseId, status, pageable);

        return ResponseEntity.ok(ApiResponse.<PaginationResponse<EnrollmentResponse>>builder()
                .status("success")
                .data(PaginationResponse.<EnrollmentResponse>builder()
                        .content(enrollmentsPage.getContent())
                        .totalElements(enrollmentsPage.getTotalElements())
                        .totalPages(enrollmentsPage.getTotalPages())
                        .currentPage(enrollmentsPage.getNumber())
                        .size(enrollmentsPage.getSize())
                        .build())
                .build());
    }
}