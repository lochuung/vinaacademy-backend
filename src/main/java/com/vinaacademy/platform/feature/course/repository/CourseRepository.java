package com.vinaacademy.platform.feature.course.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.vinaacademy.platform.feature.course.entity.Course;

public interface CourseRepository extends JpaRepository<Course, UUID> {
	
    Optional<Course> findBySlug(String slug);

    boolean existsBySlug(String slug);

    @Query("SELECT c FROM Course c WHERE c.category.slug = :slug")
    List<Course> findAllCourseByCategory(@Param("slug") String slug);

    boolean existsById(UUID id);
    
    Page<Course> findAll(Pageable pageable);
    
    Page<Course> findByCategorySlug(String categorySlug, Pageable pageable);
    
    Page<Course> findByRatingGreaterThanEqual(double minRating, Pageable pageable);
    
    Page<Course> findByCategorySlugAndRatingGreaterThanEqual(String CategorySlug, double minRating, Pageable pageable);
}

