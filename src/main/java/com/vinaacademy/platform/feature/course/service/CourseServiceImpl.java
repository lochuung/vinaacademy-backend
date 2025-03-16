package com.vinaacademy.platform.feature.course.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.vinaacademy.platform.exception.BadRequestException;
import com.vinaacademy.platform.feature.category.Category;
import com.vinaacademy.platform.feature.category.repository.CategoryRepository;
import com.vinaacademy.platform.feature.category.service.CategoryService;
import com.vinaacademy.platform.feature.common.utils.SlugUtils;
import com.vinaacademy.platform.feature.course.dto.CourseDto;
import com.vinaacademy.platform.feature.course.dto.CourseRequest;
import com.vinaacademy.platform.feature.course.entity.Course;
import com.vinaacademy.platform.feature.course.enums.CourseStatus;
import com.vinaacademy.platform.feature.course.mapper.CourseMapper;
import com.vinaacademy.platform.feature.course.repository.CourseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

	private final CourseRepository courseRepository;
	private final CategoryRepository categoryRepository;
	private final CourseMapper courseMapper;
	private final CategoryService categoryService;

	@Override
	public List<CourseDto> getCourses() {
		return courseRepository.findAll().stream().map(courseMapper::toDTO).toList();
	}

	@Override
	public CourseDto getCourse(String slug) {
		Course course = courseRepository.findBySlug(slug)
				.orElseThrow(() -> BadRequestException.message("Khóa học không tồn tại"));
		return courseMapper.toDTO(course);
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
		return courseRepository.findAllCourseByCategory(slug).stream().map(course -> courseMapper.toDTO(course))
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
		} else if (minRating > 0 ) {
			coursePage = courseRepository.findByRatingGreaterThanEqual(minRating, pageable);
		} else {
			coursePage = courseRepository.findAll(pageable);
		}
		return coursePage.map(courseMapper::toDTO);
	}

}
