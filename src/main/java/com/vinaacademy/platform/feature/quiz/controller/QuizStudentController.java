package com.vinaacademy.platform.feature.quiz.controller;

import com.vinaacademy.platform.feature.quiz.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/quiz")
@RequiredArgsConstructor
public class QuizStudentController {
    private final QuizService quizService;
}
