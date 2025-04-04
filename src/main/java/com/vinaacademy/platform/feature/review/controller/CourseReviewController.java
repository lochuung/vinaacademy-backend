package com.vinaacademy.platform.feature.review.controller;

import com.vinaacademy.platform.feature.common.response.ApiResponse;
import com.vinaacademy.platform.feature.review.dto.CourseReviewDto;
import com.vinaacademy.platform.feature.review.dto.CourseReviewRequestDto;
import com.vinaacademy.platform.feature.review.service.CourseReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/course-reviews")
@RequiredArgsConstructor
@Tag(name = "Course Review API", description = "API đánh giá khóa học")
public class CourseReviewController {
    private final CourseReviewService courseReviewService;

    @Operation(summary = "Tạo hoặc cập nhật đánh giá khóa học")
    @PostMapping
    public ResponseEntity<ApiResponse<CourseReviewDto>> createOrUpdateReview(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CourseReviewRequestDto requestDto) {

        UUID userId = extractUserId(userDetails);
        CourseReviewDto reviewDto = courseReviewService.createOrUpdateReview(userId, requestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("success", "Đánh giá khóa học thành công", reviewDto));
    }

    @Operation(summary = "Lấy danh sách đánh giá của một khóa học")
    @GetMapping("/course/{courseId}")
    public ResponseEntity<ApiResponse<Page<CourseReviewDto>>> getCourseReviews(
            @PathVariable UUID courseId,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {

        Page<CourseReviewDto> reviews = courseReviewService.getCourseReviews(courseId, pageable);

        return ResponseEntity.ok(new ApiResponse<>("success", "Lấy danh sách đánh giá thành công", reviews));
    }

    @Operation(summary = "Lấy danh sách đánh giá của người dùng hiện tại")
    @GetMapping("/user")
    public ResponseEntity<ApiResponse<List<CourseReviewDto>>> getUserReviews(
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID userId = extractUserId(userDetails);
        List<CourseReviewDto> reviews = courseReviewService.getUserReviews(userId);

        return ResponseEntity.ok(new ApiResponse<>("success", "Lấy danh sách đánh giá của bạn thành công", reviews));
    }

    @Operation(summary = "Lấy chi tiết một đánh giá")
    @GetMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<CourseReviewDto>> getReviewById(
            @PathVariable Long reviewId) {

        CourseReviewDto review = courseReviewService.getReviewById(reviewId);

        return ResponseEntity.ok(new ApiResponse<>("success", "Lấy thông tin đánh giá thành công", review));
    }

    @Operation(summary = "Lấy đánh giá của người dùng hiện tại cho một khóa học")
    @GetMapping("/user/course/{courseId}")
    public ResponseEntity<ApiResponse<CourseReviewDto>> getUserReviewForCourse(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID courseId) {

        UUID userId = extractUserId(userDetails);
        CourseReviewDto review = courseReviewService.getUserReviewForCourse(userId, courseId);

        if (review == null) {
            return ResponseEntity.ok(new ApiResponse<>("success", "Bạn chưa đánh giá khóa học này", null));
        }

        return ResponseEntity.ok(new ApiResponse<>("success", "Lấy đánh giá của bạn thành công", review));
    }

    @Operation(summary = "Xóa đánh giá")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long reviewId) {

        UUID userId = extractUserId(userDetails);
        courseReviewService.deleteReview(userId, reviewId);

        return ResponseEntity.ok(new ApiResponse<>("success", "Xóa đánh giá thành công", null));
    }

    @Operation(summary = "Lấy thống kê đánh giá của một khóa học")
    @GetMapping("/statistics/course/{courseId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCourseReviewStatistics(
            @PathVariable UUID courseId) {

        Map<String, Object> statistics = courseReviewService.getCourseReviewStatistics(courseId);

        return ResponseEntity.ok(new ApiResponse<>("success", "Lấy thống kê đánh giá thành công", statistics));
    }

    @Operation(summary = "Kiểm tra người dùng đã đánh giá khóa học chưa")
    @GetMapping("/check/course/{courseId}")
    public ResponseEntity<ApiResponse<Boolean>> hasUserReviewedCourse(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID courseId) {

        UUID userId = extractUserId(userDetails);
        boolean hasReviewed = courseReviewService.hasUserReviewedCourse(userId, courseId);

        return ResponseEntity.ok(new ApiResponse<>("success",
                hasReviewed ? "Bạn đã đánh giá khóa học này" : "Bạn chưa đánh giá khóa học này",
                hasReviewed));
    }

    private UUID extractUserId(UserDetails userDetails) {
        return UUID.fromString(userDetails.getUsername());
    }
}
