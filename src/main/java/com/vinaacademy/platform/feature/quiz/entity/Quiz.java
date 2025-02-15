package com.vinaacademy.platform.feature.quiz.entity;

import com.vinaacademy.platform.feature.course.entity.Lesson;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Data
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("QUIZ")
public class Quiz extends Lesson {
    @Column(name = "description")
    private String description;

    @Column(name = "totalPoint")
    private double totalPoint = 0;

    @Column(name = "passPoint")
    private double passPoint = 0;

    @Column(name = "duration")
    private int duration = 0;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();

    public void addQuestion(Question question) {
        questions.add(question);
        question.setQuiz(this);
    }

    public void removeQuestion(Question question) {
        questions.remove(question);
        question.setQuiz(null);
    }
}
