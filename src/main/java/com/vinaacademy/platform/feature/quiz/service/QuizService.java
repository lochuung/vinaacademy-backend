package com.vinaacademy.platform.feature.quiz.service;

import com.vinaacademy.platform.feature.quiz.dto.*;

import java.util.List;
import java.util.UUID;

public interface QuizService {
    /**
     * Get a quiz by ID
     */
    QuizDto getQuizByIdForInstructor(UUID id);
    
    /**
     * Get a quiz for student view (hiding correct answers)
     */
    QuizDto getQuizForStudent(UUID id);
    
    /**
     * Get all quizzes for a course
     */
    List<QuizDto> getQuizzesByCourseId(UUID courseId);
    
    /**
     * Get all quizzes for a section
     */
    List<QuizDto> getQuizzesBySectionId(UUID sectionId);
    
    /**
     * Create a new question for a quiz
     */
    QuestionDto createQuestion(UUID quizId, QuestionDto questionDto);
    
    /**
     * Update an existing question
     */
    QuestionDto updateQuestion(UUID questionId, QuestionDto questionDto);
    
    /**
     * Delete a question
     */
    void deleteQuestion(UUID questionId);
    
    /**
     * Create an answer for a question
     */
    AnswerDto createAnswer(UUID questionId, AnswerDto answerDto);
    
    /**
     * Update an existing answer
     */
    AnswerDto updateAnswer(UUID answerId, AnswerDto answerDto);
    
    /**
     * Delete an answer
     */
    void deleteAnswer(UUID answerId);
    
    /**
     * Submit a quiz attempt as a student
     */
    QuizSubmissionResultDto submitQuiz(QuizSubmissionRequest request);
    
    /**
     * Get a student's latest submission for a quiz
     */
    QuizSubmissionResultDto getLatestSubmission(UUID quizId);
    
    /**
     * Get all submissions for a quiz by a student
     */
    List<QuizSubmissionResultDto> getSubmissionHistory(UUID quizId);
    
    /**
     * Get all student submissions for a quiz (instructor view)
     */
    List<QuizSubmissionResultDto> getQuizSubmissions(UUID quizId);
}
