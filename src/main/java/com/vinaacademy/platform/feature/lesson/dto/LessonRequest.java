package com.vinaacademy.platform.feature.lesson.dto;

import com.vinaacademy.platform.feature.course.enums.LessonType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonRequest {
    
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title cannot exceed 255 characters")
    private String title;
    
    @NotNull(message = "Section ID is required")
    private UUID sectionId;
    
    @NotNull(message = "Lesson type is required")
    private LessonType type;

    private String description;
    
    private boolean free;
    
    @Min(value = 0, message = "Order index cannot be negative")
    private int orderIndex;
    
    // Fields specific to lesson types

    // For Video lessons
    private String thumbnailUrl;
    
    // For Reading lessons
    private String content;
    
    // For Quiz lessons
    @Min(value = 0, message = "Pass point cannot be negative")
    private Double passPoint;
    
    @Min(value = 0, message = "Total point cannot be negative")
    private Double totalPoint;
    
    @Min(value = 1, message = "Duration must be positive")
    private Integer duration;
}
