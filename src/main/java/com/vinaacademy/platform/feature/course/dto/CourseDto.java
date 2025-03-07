package com.vinaacademy.platform.feature.course.dto;

import java.math.BigDecimal;
import java.util.UUID;
import com.vinaacademy.platform.feature.common.dto.BaseDto;
import com.vinaacademy.platform.feature.course.enums.CourseLevel;
import com.vinaacademy.platform.feature.course.enums.CourseStatus;

import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = false)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDto extends BaseDto{
	
	private UUID id;

    private String image;

    private String name;

    private String description;

    private String slug;

    private BigDecimal price;

    private CourseLevel level;

    private CourseStatus status;

    private String language ;

    private String categoryName;

    private double rating;

    private long totalRating;

    private long totalStudent;

    private long totalSection;

    private long totalLesson; 
	
}
