package com.vinaacademy.platform.feature.quiz;

import com.vinaacademy.platform.feature.lesson.Lesson;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import lombok.Getter;
import jakarta.persistence.Entity;
import lombok.Setter;

@Entity
@DiscriminatorValue("QUIZ")
@Getter
@Setter
public class Quiz extends Lesson {
    @Column(name = "description")
    private String description;

    @Column(name = "totalPoint")
    private double totalPoint = 0;

    @Column(name = "passPoint")
    private double passPoint = 0;

    @Column(name = "duration")
    private int duration = 0;
}
