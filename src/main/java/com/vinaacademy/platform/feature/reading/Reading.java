package com.vinaacademy.platform.feature.reading;

import com.vinaacademy.platform.feature.lesson.Lesson;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("READING")
@Getter
@Setter
public class Reading extends Lesson {
    @Column(name = "content")
    private String content;
}
