package com.vinaacademy.platform.feature.lesson.mapper;

import com.vinaacademy.platform.feature.lesson.dto.LessonDto;
import com.vinaacademy.platform.feature.lesson.entity.Lesson;
import com.vinaacademy.platform.feature.quiz.entity.Quiz;
import com.vinaacademy.platform.feature.reading.Reading;
import com.vinaacademy.platform.feature.video.entity.Video;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LessonMapper {

    @Mapping(target = "sectionId", source = "section.id")
    @Mapping(target = "sectionTitle", source = "section.title")
    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "authorName", source = "author.fullName")
    @Mapping(target = "courseId", source = "section.course.id")
    @Mapping(target = "courseName", source = "section.course.name")
    @Mapping(target = "thumbnailUrl", ignore = true)
    @Mapping(target = "videoUrl", ignore = true)
    @Mapping(target = "videoDuration", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "content", ignore = true)
    @Mapping(target = "passPoint", ignore = true)
    @Mapping(target = "totalPoint", ignore = true)
    @Mapping(target = "duration", ignore = true)
    LessonDto lessonToLessonDto(Lesson lesson);

    @AfterMapping
    default void mapSpecificFields(@MappingTarget LessonDto.LessonDtoBuilder<?, ?> builder, Lesson lesson) {
        if (lesson instanceof Video video) {
            builder.thumbnailUrl(video.getThumbnailUrl());
            builder.videoDuration(video.getDuration());
            builder.status(video.getStatus());
        } else if (lesson instanceof Reading reading) {
            builder.content(reading.getContent());
        } else if (lesson instanceof Quiz quiz) {
            builder.passPoint(quiz.getPassPoint());
            builder.totalPoint(quiz.getTotalPoint());
            builder.duration(quiz.getDuration());
        }
    }
}
