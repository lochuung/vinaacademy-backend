package com.vinaacademy.platform.feature.course.service;

import com.vinaacademy.platform.exception.BadRequestException;
import com.vinaacademy.platform.feature.category.Category;
import com.vinaacademy.platform.feature.category.repository.CategoryRepository;
import com.vinaacademy.platform.feature.category.service.CategoryService;
import com.vinaacademy.platform.feature.common.utils.SlugUtils;
import com.vinaacademy.platform.feature.course.dto.CourseDto;
import com.vinaacademy.platform.feature.course.dto.CourseRequest;
import com.vinaacademy.platform.feature.course.dto.CourseSearchRequest;
import com.vinaacademy.platform.feature.course.dto.CourseDetailsResponse;
import com.vinaacademy.platform.feature.course.entity.Course;
import com.vinaacademy.platform.feature.course.enums.CourseStatus;
import com.vinaacademy.platform.feature.course.mapper.CourseMapper;
import com.vinaacademy.platform.feature.course.repository.CourseRepository;
import com.vinaacademy.platform.feature.course.repository.specification.CourseSpecification;
import com.vinaacademy.platform.feature.instructor.CourseInstructor;
import com.vinaacademy.platform.feature.instructor.repository.CourseInstructorRepository;
import com.vinaacademy.platform.feature.lesson.mapper.LessonMapper;
import com.vinaacademy.platform.feature.review.mapper.CourseReviewMapper;
import com.vinaacademy.platform.feature.section.entity.Section;
import com.vinaacademy.platform.feature.section.dto.SectionDto;
import com.vinaacademy.platform.feature.section.mapper.SectionMapper;
import com.vinaacademy.platform.feature.review.dto.CourseReviewDto;
import com.vinaacademy.platform.feature.user.UserMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final CourseMapper courseMapper;
    private final CategoryService categoryService;
    private final CourseInstructorRepository courseInstructorRepository;
    private final SectionMapper sectionMapper;
    private final LessonMapper lessonMapper;

    @Override
    public List<CourseDto> getCourses() {
        return courseRepository.findAll().stream().map(courseMapper::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CourseDetailsResponse getCourse(String slug) {
        Course course = courseRepository.findBySlug(slug)
                .orElseThrow(() -> BadRequestException.message("Khóa học không tồn tại"));

        // Use CourseMapper to create the base course details
        CourseDetailsResponse response = courseMapper.toCourseDetailsResponse(course);
        
        // Fetch and set instructors
        List<CourseInstructor> courseInstructors = courseInstructorRepository.findByCourse(course);
        response.setInstructors(courseInstructors.stream()
                .map(ci -> UserMapper.INSTANCE.toDto(ci.getInstructor()))
                .toList());
        
        // Find the owner instructor specifically
        courseInstructorRepository.findByCourseAndIsOwnerTrue(course)
                .ifPresent(owner -> response.setOwnerInstructor(UserMapper.INSTANCE.toDto(owner.getInstructor())));
        
        // Fetch sections with lessons
        List<Section> sections = course.getSections();
        if (sections != null && !sections.isEmpty()) {
            List<SectionDto> sectionDtos = sections.stream()
                    .sorted(java.util.Comparator.comparing(Section::getOrderIndex))
                    .map(section -> {
                        SectionDto sectionDto = sectionMapper.toDto(section);
                        // Fetch and map lessons for each section
                        sectionDto.setLessons(section.getLessons().stream()
                                .sorted(java.util.Comparator.comparing(lesson -> lesson.getOrderIndex()))
                                .map(lessonMapper::lessonToLessonDto)
                                .toList());
                        return sectionDto;
                    })
                    .toList();
            response.setSections(sectionDtos);
        }
        
        // Fetch course reviews
        if (course.getCourseReviews() != null && !course.getCourseReviews().isEmpty()) {
            List<CourseReviewDto> reviewDtos = course.getCourseReviews().stream()
                    .map(review -> CourseReviewMapper.INSTANCE.toDto(review))
                    .toList();
            response.setReviews(reviewDtos);
        }
        
        return response;
    }

    @Override
    public CourseDto createCourse(CourseRequest request) {
        String slug = StringUtils.isBlank(request.getSlug()) ? request.getSlug() : SlugUtils.toSlug(request.getName());

        if (courseRepository.existsBySlug(slug)) {
            throw BadRequestException.message("Slug url đã tồn tại");
        }
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> BadRequestException.message("Không tìm thấy danh mục"));
        Course course = Course.builder()
                .name(request.getName())
                .category(category)
                .description(request.getDescription())
                .image(request.getImage())
                .language(request.getLanguage())
                .level(request.getLevel())
                .price(request.getPrice())
                .rating(0)
                .slug(slug)
                .status(CourseStatus.PENDING)
                .build();

        courseRepository.save(course);

        return courseMapper.toDTO(course);
    }

    @Override
    public CourseDto updateCourse(String slug, CourseRequest request) {
        Course course = courseRepository.findBySlug(slug)
                .orElseThrow(() -> BadRequestException.message("Khóa học không tồn tại"));

        String newSlug = StringUtils.isBlank(request.getSlug()) ? request.getSlug()
                : SlugUtils.toSlug(request.getName());

        if (!slug.equals(newSlug) && courseRepository.existsBySlug(newSlug)) {
            throw BadRequestException.message("Slug đã tồn tại");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> BadRequestException.message("Không tìm thấy danh mục"));
        course.setName(request.getName());
        course.setSlug(newSlug);
        course.setCategory(category);
        course.setDescription(request.getDescription());
        course.setImage(request.getImage());
        course.setLanguage(request.getLanguage());
        course.setLevel(request.getLevel());
        course.setPrice(request.getPrice());
        course.setStatus(request.getStatus());
        course.setRating(request.getRating());
        courseRepository.save(course);
        return courseMapper.toDTO(course);
    }

    @Override
    public void deleteCourse(String slug) {
        Course course = courseRepository.findBySlug(slug)
                .orElseThrow(() -> BadRequestException.message("Khóa học không tồn tại"));

        courseRepository.delete(course);
    }

    @Override
    public List<CourseDto> getCoursesByCategory(String slug) {
        categoryRepository.findBySlug(slug).orElseThrow(() -> BadRequestException.message("Danh mục không tồn tại"));
        return courseRepository.findAllCourseByCategory(slug).stream().map(courseMapper::toDTO)
                .toList();
    }

    @Override
    public Page<CourseDto> getCoursesPaginated(int page, int size, String sortBy, String sortDirection,
                                               String categorySlug, double minRating) {
        Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<Course> coursePage;
        if (!StringUtils.isBlank(categorySlug) && minRating > 0) {
            coursePage = courseRepository.findByCategorySlugAndRatingGreaterThanEqual(categorySlug, minRating,
                    pageable);
        } else if (!StringUtils.isBlank(categorySlug)) {
            coursePage = courseRepository.findByCategorySlug(categorySlug, pageable);
        } else if (minRating > 0) {
            coursePage = courseRepository.findByRatingGreaterThanEqual(minRating, pageable);
        } else {
            coursePage = courseRepository.findAll(pageable);
        }
        return coursePage.map(courseMapper::toDTO);
    }

    @Override
    public Page<CourseDto> searchCourses(CourseSearchRequest searchRequest, int page, int size,
                                         String sortBy, String sortDirection) {
        Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        // Build specification dynamically using the utility class
        Specification<Course> spec = Specification.where(CourseSpecification.hasKeyword(searchRequest.getKeyword()))
                .and(CourseSpecification.hasStatus(searchRequest.getStatus() != null ? searchRequest.getStatus() : CourseStatus.PUBLISHED))
                .and(CourseSpecification.hasCategory(searchRequest.getCategorySlug()))
                .and(CourseSpecification.hasLevel(searchRequest.getLevel()))
                .and(CourseSpecification.hasLanguage(searchRequest.getLanguage()))
                .and(CourseSpecification.hasMinPrice(searchRequest.getMinPrice()))
                .and(CourseSpecification.hasMaxPrice(searchRequest.getMaxPrice()))
                .and(CourseSpecification.hasMinRating(searchRequest.getMinRating()));

        Page<Course> coursePage = courseRepository.findAll(spec, pageable);
        return coursePage.map(courseMapper::toDTO);
    }

}
