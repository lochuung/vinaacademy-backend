package com.vinaacademy.platform.feature.reading;

import com.vinaacademy.platform.feature.course.entity.Lesson;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("READING")
public class Reading extends Lesson {
    @Column(name = "content")
    private String content;
}
