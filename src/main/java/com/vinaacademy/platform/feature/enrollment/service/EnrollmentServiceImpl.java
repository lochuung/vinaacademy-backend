package com.vinaacademy.platform.feature.enrollment.service;

import com.vinaacademy.platform.feature.common.exception.ResourceNotFoundException;
import com.vinaacademy.platform.feature.course.entity.Course;
import com.vinaacademy.platform.feature.course.repository.CourseRepository;
import com.vinaacademy.platform.feature.enrollment.Enrollment;
import com.vinaacademy.platform.feature.enrollment.dto.EnrollmentRequest;
import com.vinaacademy.platform.feature.enrollment.dto.EnrollmentResponse;
import com.vinaacademy.platform.feature.enrollment.enums.ProgressStatus;
import com.vinaacademy.platform.feature.enrollment.mapper.EnrollmentMapper;
import com.vinaacademy.platform.feature.enrollment.repository.EnrollmentRepository;
import com.vinaacademy.platform.feature.user.UserRepository;
import com.vinaacademy.platform.feature.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentMapper enrollmentMapper;

    @Override
    @Transactional
    public EnrollmentResponse enrollCourse(EnrollmentRequest request, UUID userId) {
        // Kiểm tra xem người dùng đã đăng ký khóa học này chưa
        if (enrollmentRepository.existsByUserIdAndCourseId(userId, request.getCourseId())) {
            throw new IllegalStateException("Bạn đã đăng ký khóa học này rồi");
        }

        // Lấy thông tin người dùng
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        // Lấy thông tin khóa học
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học"));

        // Tạo đăng ký mới
        Enrollment enrollment = Enrollment.builder()
                .user(user)
                .course(course)
                .progressPercentage(0.0)
                .status(ProgressStatus.IN_PROGRESS)
                .startAt(LocalDateTime.now())
                .build();

        // Lưu vào database
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);

        // Cập nhật số lượng học viên của khóa học
        course.setTotalStudent(course.getTotalStudent() + 1);
        courseRepository.save(course);

        // Chuyển đổi và trả về response
        return enrollmentMapper.toDto(savedEnrollment);
    }

    @Override
    @Transactional(readOnly = true)
    public EnrollmentResponse getEnrollment(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đăng ký khóa học"));
        return enrollmentMapper.toDto(enrollment);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEnrolled(UUID userId, UUID courseId) {
        return enrollmentRepository.existsByUserIdAndCourseId(userId, courseId);
    }

    @Override
    public List<EnrollmentResponse> getUserEnrollments(UUID userId) {
        List<Enrollment> enrollments = enrollmentRepository.findByUserId(userId);
        return enrollments.stream()
                .map(enrollmentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EnrollmentResponse> getUserEnrollments(UUID userId, Pageable pageable) {
        Page<Enrollment> enrollments = enrollmentRepository.findByUserId(userId, pageable);
        return enrollments.map(enrollmentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getUserEnrollmentsByStatus(UUID userId, ProgressStatus status) {
        List<Enrollment> enrollments = enrollmentRepository.findByUserIdAndStatus(userId, status);
        return enrollments.stream()
                .map(enrollmentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EnrollmentResponse updateProgress(Long enrollmentId, Double progressPercentage) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đăng ký khóa học"));

        enrollment.setProgressPercentage(progressPercentage);

        // Nếu tiến độ đạt 100%, đánh dấu là đã hoàn thành
        if (progressPercentage >= 100.0) {
            enrollment.setStatus(ProgressStatus.COMPLETED);
            enrollment.setCompleteAt(LocalDateTime.now());
        }

        Enrollment updatedEnrollment = enrollmentRepository.save(enrollment);
        return enrollmentMapper.toDto(updatedEnrollment);
    }

    @Override
    @Transactional
    public EnrollmentResponse updateStatus(Long enrollmentId, ProgressStatus status) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đăng ký khóa học"));

        enrollment.setStatus(status);

        // Nếu trạng thái là hoàn thành, cập nhật thời gian hoàn thành
        if (status == ProgressStatus.COMPLETED) {
            enrollment.setCompleteAt(LocalDateTime.now());
            enrollment.setProgressPercentage(100.0);
        }

        Enrollment updatedEnrollment = enrollmentRepository.save(enrollment);
        return enrollmentMapper.toDto(updatedEnrollment);
    }

    @Override
    @Transactional
    public void cancelEnrollment(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đăng ký khóa học"));

        // Cập nhật số lượng học viên của khóa học
        Course course = enrollment.getCourse();
        course.setTotalStudent(course.getTotalStudent() - 1);
        courseRepository.save(course);

        // Xóa đăng ký
        enrollmentRepository.delete(enrollment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EnrollmentResponse> getCourseEnrollments(UUID courseId, ProgressStatus status, Pageable pageable) {
        Page<Enrollment> enrollmentsPage;

        if (status != null) {
            // Cần thêm phương thức này vào repository
            enrollmentsPage = enrollmentRepository.findByCourseIdAndStatus(courseId, status, pageable);
        } else {
            enrollmentsPage = enrollmentRepository.findByCourseId(courseId, pageable);
        }

        return enrollmentsPage.map(enrollmentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEnrollmentOwnerByUser(Long enrollmentId, UUID userId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đăng ký khóa học"));
        return enrollment.getUser().getId().equals(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isCourseOwnerByInstructor(UUID courseId, UUID instructorId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học"));

        // Kiểm tra xem instructorId có nằm trong danh sách giảng viên của khóa học không
        return course.getInstructors().stream()
                .anyMatch(instructor -> instructor.getId().equals(instructorId));
    }

    @Transactional(readOnly = true)
    @Override
    public boolean isEnrollmentInCourseOfInstructor(Long enrollmentId, UUID instructorId) {
        // Tìm enrollment
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đăng ký khóa học"));

        // Lấy courseId từ enrollment
        UUID courseId = enrollment.getCourse().getId();

        // Kiểm tra xem instructor có phải là người dạy khóa học này không
        return isCourseOwnerByInstructor(courseId, instructorId);
    }
}