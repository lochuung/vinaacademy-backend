package com.vinaacademy.platform.feature.section.service;

import com.vinaacademy.platform.exception.BadRequestException;
import com.vinaacademy.platform.exception.NotFoundException;
import com.vinaacademy.platform.feature.section.dto.SectionDto;
import com.vinaacademy.platform.feature.section.dto.SectionRequest;
import com.vinaacademy.platform.feature.course.entity.Course;
import com.vinaacademy.platform.feature.section.entity.Section;
import com.vinaacademy.platform.feature.section.mapper.SectionMapper;
import com.vinaacademy.platform.feature.course.repository.CourseRepository;
import com.vinaacademy.platform.feature.section.repository.SectionRepository;
import com.vinaacademy.platform.feature.instructor.repository.CourseInstructorRepository;
import com.vinaacademy.platform.feature.user.auth.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SectionServiceImpl implements SectionService {

    private final SectionRepository sectionRepository;
    private final CourseRepository courseRepository;
    private final CourseInstructorRepository courseInstructorRepository;
    private final SectionMapper sectionMapper;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional(readOnly = true)
    public List<SectionDto> getSectionsByCourse(UUID courseId) {
        log.debug("Getting sections for course id: {}", courseId);
        Course course = findCourseById(courseId);
        List<Section> sections = sectionRepository.findByCourseOrderByOrderIndex(course);
        return sectionMapper.toDtoList(sections);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SectionDto> getSectionsByCourseSlug(String courseSlug) {
        log.debug("Getting sections for course slug: {}", courseSlug);
        Course course = findCourseBySlug(courseSlug);
        List<Section> sections = sectionRepository.findByCourseOrderByOrderIndex(course);
        return sectionMapper.toDtoList(sections);
    }

    @Override
    @Transactional(readOnly = true)
    public SectionDto getSectionById(UUID id) {
        log.debug("Getting section by id: {}", id);
        Section section = findSectionById(id);
        return sectionMapper.toDto(section);
    }

    @Override
    @Transactional
    public SectionDto createSection(SectionRequest request) {
        log.debug("Creating section: {}", request);
        Course course = findCourseById(request.getCourseId());
        
        // Check if user has permission to modify this course
        checkCoursePermission(course);
        
        // Validate section order index
        validateOrderIndex(request.getOrderIndex(), course, null);
        
        // Check for duplicate title in the same course
        if (sectionRepository.existsByTitleAndCourse(request.getTitle(), course)) {
            throw BadRequestException.message("Tiêu đề mục đã tồn tại trong khóa học này");
        }
        
        Section section = Section.createSection(
                null,
                course,
                request.getTitle(),
                request.getOrderIndex(),
                null
        );
        
        section = sectionRepository.save(section);
        log.info("Section created with id: {}", section.getId());
        
        return sectionMapper.toDto(section);
    }

    @Override
    @Transactional
    public SectionDto updateSection(UUID id, SectionRequest request) {
        log.debug("Updating section with id: {}", id);
        Section section = findSectionById(id);
        Course course = findCourseById(request.getCourseId());
        
        // Check if user has permission to modify this course
        checkCoursePermission(course);
        
        // Validate section order index
        validateOrderIndex(request.getOrderIndex(), course, id);
        
        // Check for duplicate title in the same course (excluding this section)
        if (!section.getTitle().equals(request.getTitle()) && 
                sectionRepository.existsByTitleAndCourse(request.getTitle(), course)) {
            throw BadRequestException.message("Tiêu đề mục đã tồn tại trong khóa học này");
        }
        
        section.setTitle(request.getTitle());
        section.setOrderIndex(request.getOrderIndex());
        
        // Only change course if different from current course
        if (!section.getCourse().getId().equals(course.getId())) {
            section.setCourse(course);
        }
        
        section = sectionRepository.save(section);
        log.info("Section updated with id: {}", section.getId());
        
        return sectionMapper.toDto(section);
    }

    @Override
    @Transactional
    public void deleteSection(UUID id) {
        log.debug("Deleting section with id: {}", id);
        Section section = findSectionById(id);
        
        // Check if user has permission to modify this course
        checkCoursePermission(section.getCourse());
        
        // Check if section has lessons
        if (!section.getLessons().isEmpty()) {
            throw BadRequestException.message("Không thể xóa mục có bài học. Xóa tất cả bài học trước");
        }
        
        sectionRepository.delete(section);
        log.info("Section deleted with id: {}", id);
    }

    @Override
    @Transactional
    public void reorderSections(UUID courseId, List<UUID> sectionIds) {
        log.debug("Reordering sections for course id: {}", courseId);
        Course course = findCourseById(courseId);
        reorderSectionsForCourse(course, sectionIds);
    }
    
    @Override
    @Transactional
    public void reorderSectionsBySlug(String courseSlug, List<UUID> sectionIds) {
        log.debug("Reordering sections for course slug: {}", courseSlug);
        Course course = findCourseBySlug(courseSlug);
        reorderSectionsForCourse(course, sectionIds);
    }
    
    private void reorderSectionsForCourse(Course course, List<UUID> sectionIds) {
        // Check if user has permission to modify this course
        checkCoursePermission(course);
        
        // Get all sections for the course
        List<Section> sections = sectionRepository.findByCourseOrderByOrderIndex(course);
        
        // Validate that all section IDs belong to the course
        Set<UUID> courseSectionIds = sections.stream().map(Section::getId).collect(Collectors.toSet());
        if (!courseSectionIds.containsAll(sectionIds)) {
            throw BadRequestException.message("Danh sách ID không hợp lệ");
        }
        
        // Validate that all sections are included
        if (sections.size() != sectionIds.size()) {
            throw BadRequestException.message("Danh sách không đầy đủ các mục");
        }
        
        // Create a map for quick lookup
        Map<UUID, Section> sectionMap = new HashMap<>();
        sections.forEach(section -> sectionMap.put(section.getId(), section));
        
        // Update order index for each section
        for (int i = 0; i < sectionIds.size(); i++) {
            UUID sectionId = sectionIds.get(i);
            Section section = sectionMap.get(sectionId);
            section.setOrderIndex(i);
            sectionRepository.save(section);
        }
        
        log.info("Sections reordered for course: {}", course.getName());
    }

    private Section findSectionById(UUID id) {
        return sectionRepository.findById(id)
                .orElseThrow(() -> NotFoundException.message("Không tìm thấy mục với id: " + id));
    }

    private Course findCourseById(UUID id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> NotFoundException.message("Không tìm thấy khóa học với id: " + id));
    }
    
    private Course findCourseBySlug(String slug) {
        return courseRepository.findBySlug(slug)
                .orElseThrow(() -> NotFoundException.message("Không tìm thấy khóa học với slug: " + slug));
    }
    
    private void checkCoursePermission(Course course) {
        UUID currentUserId = securityUtils.getCurrentUser().getId();
        
        // Check if user is instructor of the course
        boolean isInstructor = courseInstructorRepository.existsByInstructorIdAndCourseId(
                currentUserId, course.getId());
        
        if (!isInstructor) {
            throw BadRequestException.message("Bạn không có quyền chỉnh sửa khóa học này");
        }
    }
    
    private void validateOrderIndex(int orderIndex, Course course, UUID sectionId) {
        List<Section> existingSections = sectionRepository.findByCourseOrderByOrderIndex(course);
        
        // For updates, exclude the current section from duplicate check
        if (sectionId != null) {
            existingSections = existingSections.stream()
                    .filter(section -> !section.getId().equals(sectionId))
                    .collect(Collectors.toList());
        }
        
        // Check if order index is in valid range
        int maxOrderIndex = existingSections.size();
        if (orderIndex < 0 || orderIndex > maxOrderIndex) {
            throw BadRequestException.message(
                    "Chỉ số thứ tự không hợp lệ. Giá trị phải từ 0 đến " + maxOrderIndex);
        }
    }
}
