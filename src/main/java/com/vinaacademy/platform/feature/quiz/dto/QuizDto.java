package com.vinaacademy.platform.feature.quiz.dto;

import com.vinaacademy.platform.feature.lesson.dto.LessonDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class QuizDto extends LessonDto {
    private Double totalPoint;
    private Double passPoint;
    private Integer duration;
    
    // Quiz settings
    private Boolean randomizeQuestions;
    private Boolean showCorrectAnswers;
    private Boolean allowRetake;
    private Boolean requirePassingScore;
    private Double passingScore;
    private Integer timeLimit;
    
    private List<QuestionDto> questions = new ArrayList<>();
}