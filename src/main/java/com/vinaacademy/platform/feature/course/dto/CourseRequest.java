package com.vinaacademy.platform.feature.course.dto;


import com.vinaacademy.platform.feature.course.enums.CourseLevel;
import com.vinaacademy.platform.feature.course.enums.CourseStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseRequest {

    private String image;
    
    @NotBlank(message = "Tên khóa học không được để trống")
    private String name;
    
    private String description;
    
    private String slug;

    private BigDecimal price;

    private CourseLevel level;

    private CourseStatus status;

    private String language;

    private Long categoryId;

    private double rating;

    private long totalRating;

    private long totalStudent;

    private long totalSection;

    private long totalLesson; 
}