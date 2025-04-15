package com.vinaacademy.platform.feature.quiz.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizSubmissionResultDto {
    private UUID id;
    private UUID quizId;
    private String quizTitle;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double score;
    private Double totalPoints;
    private Boolean isPassed;
    private List<UserAnswerResultDto> answers = new ArrayList<>();
}

//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//class UserAnswerResultDto {
//    private UUID questionId;
//    private String questionText;
//    private Double points;
//    private Double earnedPoints;
//    private Boolean isCorrect;
//    private List<AnswerResultDto> answers = new ArrayList<>();
//    private String textAnswer;
//}
//
//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//class AnswerResultDto {
//    private UUID id;
//    private String text;
//    private Boolean isSelected;
//    private Boolean isCorrect;
//}