package com.vinaacademy.platform.feature.lesson.factory;

import com.vinaacademy.platform.exception.ValidationException;
import com.vinaacademy.platform.feature.lesson.dto.LessonRequest;
import com.vinaacademy.platform.feature.lesson.entity.Lesson;
import com.vinaacademy.platform.feature.quiz.entity.Quiz;
import com.vinaacademy.platform.feature.quiz.repository.QuizRepository;
import com.vinaacademy.platform.feature.section.entity.Section;
import com.vinaacademy.platform.feature.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Concrete creator for creating Quiz lessons
 */
@Component
@RequiredArgsConstructor
public class QuizLessonCreator extends LessonCreator {

    private final QuizRepository quizRepository;
    
    @Override
    public Lesson createLesson(String title, Section section, User author, boolean isFree, int orderIndex) {
        Quiz quiz = Quiz.builder()
                .title(title)
                .section(section)
                .free(isFree)
                .orderIndex(orderIndex)
                .author(author)
                .passPoint(0.0)
                .totalPoint(0.0)
                .duration(30)
                .build();
        
        return quizRepository.save(quiz);
    }
    
    @Override
    public Lesson createLesson(LessonRequest request, Section section, User author) {
        validateRequest(request);
        
        Quiz quiz = Quiz.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .section(section)
                .free(request.isFree())
                .orderIndex(request.getOrderIndex())
                .author(author)
                .passPoint(request.getPassPoint())
                .totalPoint(request.getTotalPoint())
                .duration(request.getDuration())
                .build();
        
        return quizRepository.save(quiz);
    }
    
    @Override
    public Lesson updateLesson(Lesson lesson, LessonRequest request) {
        validateUpdateRequest(request);
        
        if (!(lesson instanceof Quiz quiz)) {
            throw new ValidationException("Cannot update a non-Quiz lesson with Quiz data");
        }
        
        quiz.setPassPoint(request.getPassPoint());
        quiz.setTotalPoint(request.getTotalPoint());
        quiz.setDuration(request.getDuration());
        
        return quizRepository.save(quiz);
    }
    
    @Override
    protected void validateRequest(LessonRequest request) {
        if (request.getPassPoint() == null || request.getTotalPoint() == null || request.getDuration() == null) {
            throw new ValidationException("Pass point, total point, and duration are required for quiz lessons");
        }

        if (request.getPassPoint() < 0 || request.getTotalPoint() < 0) {
            throw new ValidationException("Points cannot be negative");
        }

        if (request.getPassPoint() > request.getTotalPoint()) {
            throw new ValidationException("Pass point cannot be greater than total point");
        }

        if (request.getDuration() <= 0) {
            throw new ValidationException("Duration must be positive");
        }
    }
    
    @Override
    protected void validateUpdateRequest(LessonRequest request) {
        if (request.getPassPoint() == null || request.getTotalPoint() == null || request.getDuration() == null) {
            throw new ValidationException("Pass point, total point, and duration are required for quiz lessons");
        }

        if (request.getPassPoint() < 0 || request.getTotalPoint() < 0) {
            throw new ValidationException("Points cannot be negative");
        }

        if (request.getPassPoint() > request.getTotalPoint()) {
            throw new ValidationException("Pass point cannot be greater than total point");
        }

        if (request.getDuration() <= 0) {
            throw new ValidationException("Duration must be positive");
        }
    }
}