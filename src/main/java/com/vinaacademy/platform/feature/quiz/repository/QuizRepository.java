package com.vinaacademy.platform.feature.quiz.repository;

import com.vinaacademy.platform.feature.quiz.entity.Quiz;
import com.vinaacademy.platform.feature.section.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, UUID> {
    /**
     * Find quizzes by section ordered by their index
     */
    List<Quiz> findBySectionOrderByOrderIndex(Section section);
}
