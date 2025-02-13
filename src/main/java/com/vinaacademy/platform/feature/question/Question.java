package com.vinaacademy.platform.feature.question;

import java.util.List;
import java.util.UUID;

import com.vinaacademy.platform.feature.answer.Answer;
import com.vinaacademy.platform.feature.question.enums.QuestionType;
import com.vinaacademy.platform.feature.quiz.Quiz;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(name = "question_text")
    private String questionText;

    @Column(name = "point")
    private double point;

    @Column(name = "question_type")
    private QuestionType questionType;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answers;
}
