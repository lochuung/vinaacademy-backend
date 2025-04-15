package com.vinaacademy.platform.feature.quiz.entity;

import com.vinaacademy.platform.feature.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "answers")
public class Answer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "answer_text", columnDefinition = "TEXT")
    private String answerText;

    @Column(name = "is_correct")
    private boolean isCorrect = false;
}
