package com.vinaacademy.platform.feature.lesson.service;

import com.vinaacademy.platform.exception.BadRequestException;
import com.vinaacademy.platform.exception.NotFoundException;
import com.vinaacademy.platform.exception.ValidationException;
import com.vinaacademy.platform.feature.lesson.repository.projection.LessonAccessInfoDto;
import com.vinaacademy.platform.feature.section.entity.Section;
import com.vinaacademy.platform.feature.section.repository.SectionRepository;
import com.vinaacademy.platform.feature.lesson.entity.Lesson;
import com.vinaacademy.platform.feature.lesson.repository.LessonRepository;
import com.vinaacademy.platform.feature.lesson.dto.LessonDto;
import com.vinaacademy.platform.feature.lesson.dto.LessonRequest;
import com.vinaacademy.platform.feature.lesson.mapper.LessonMapper;
import com.vinaacademy.platform.feature.log.service.LogService;
import com.vinaacademy.platform.feature.quiz.entity.Quiz;
import com.vinaacademy.platform.feature.quiz.repository.QuizRepository;
import com.vinaacademy.platform.feature.reading.Reading;
import com.vinaacademy.platform.feature.reading.repository.ReadingRepository;
import com.vinaacademy.platform.feature.user.auth.utils.SecurityUtils;
import com.vinaacademy.platform.feature.user.constant.AuthConstants;
import com.vinaacademy.platform.feature.user.entity.User;
import com.vinaacademy.platform.feature.video.entity.Video;
import com.vinaacademy.platform.feature.video.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final SectionRepository sectionRepository;
    private final VideoRepository videoRepository;
    private final ReadingRepository readingRepository;
    private final QuizRepository quizRepository;
    private final SecurityUtils securityUtils;
    private final LogService logService;
    private final LessonMapper lessonMapper;

    @Override
    @Transactional(readOnly = true)
    public LessonDto getLessonById(UUID id) {
        log.debug("Getting lesson by id: {}", id);
        Lesson lesson = findLessonById(id);
        return lessonMapper.lessonToLessonDto(lesson);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LessonDto> getLessonsBySectionId(UUID sectionId) {
        log.debug("Getting lessons by section id: {}", sectionId);
        Section section = findSectionById(sectionId);
        return lessonRepository.findBySectionOrderByOrderIndex(section).stream()
                .map(lessonMapper::lessonToLessonDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LessonDto createLesson(LessonRequest request) {
        log.info("Creating new lesson with title: {}, type: {}", request.getTitle(), request.getType());
        User currentUser = securityUtils.getCurrentUser();
        return createLesson(request, currentUser);
    }

    @Override
    @Transactional
    public LessonDto createLesson(LessonRequest request, User author) {
        log.info("Creating new lesson with title: {}, type: {} by explicit author: {}",
                request.getTitle(), request.getType(), author.getUsername());
        Section section = findSectionById(request.getSectionId());

        validateLessonRequest(request);
        validateOrderIndex(request.getOrderIndex(), section, null);

        Lesson lesson = createLessonByType(request, section, author);

        // Log the creation
        logService.log("Lesson", "CREATE",
                String.format("Created new %s lesson in section: %s",
                        request.getType(), section.getTitle()),
                null, lessonMapper.lessonToLessonDto(lesson));

        return lessonMapper.lessonToLessonDto(lesson);
    }

    @Override
    @Transactional
    public LessonDto updateLesson(UUID id, LessonRequest request) {
        log.info("Updating lesson with id: {}", id);
        Lesson existingLesson = findLessonById(id);
        Section section = findSectionById(request.getSectionId());

        LessonDto oldLessonData = lessonMapper.lessonToLessonDto(existingLesson);

        // Check if user has permission
        if (!securityUtils.canModifyResource(existingLesson.getAuthor().getId())) {
            throw new ValidationException("You don't have permission to update this lesson");
        }

        validateLessonRequest(request);
        validateOrderIndex(request.getOrderIndex(), section, id);

        // Basic update for common fields
        existingLesson.setTitle(request.getTitle());
        existingLesson.setSection(section);
        existingLesson.setFree(request.isFree());
        existingLesson.setOrderIndex(request.getOrderIndex());

        // Specific updates based on lesson type
        if (existingLesson.getType() != request.getType()) {
            throw new ValidationException("Cannot change lesson type. Delete and create a new lesson instead.");
        }

        updateLessonByType(existingLesson, request);

        // Log the update
        logService.log("Lesson", "UPDATE",
                String.format("Updated %s lesson in section: %s",
                        request.getType(), section.getTitle()),
                oldLessonData, lessonMapper.lessonToLessonDto(existingLesson));

        return lessonMapper.lessonToLessonDto(existingLesson);
    }

    @Override
    public boolean hasAccess(UUID lessonId) {
        User currentUser = securityUtils.getCurrentUser();
        return hasAccess(lessonId, currentUser) ||
                securityUtils.hasRole(AuthConstants.ADMIN_ROLE);
    }

    @Override
    public boolean hasAccess(UUID lessonId, User user) {
        LessonAccessInfoDto lessonAccessInfo = lessonRepository
                .getLessonAccessInfo(lessonId, user.getId())
                .orElseThrow(() -> BadRequestException.message("Lesson not found"));
        return lessonAccessInfo.isFree() ||
                lessonAccessInfo.isInstructor() ||
                lessonAccessInfo.isEnrolled();
    }

    @Override
    @Transactional
    public void deleteLesson(UUID id) {
        log.info("Deleting lesson with id: {}", id);
        Lesson lesson = findLessonById(id);

        // Check if user has permission
        if (!securityUtils.canModifyResource(lesson.getAuthor().getId())) {
            throw new ValidationException("You don't have permission to delete this lesson");
        }

        LessonDto lessonData = lessonMapper.lessonToLessonDto(lesson);

        lessonRepository.delete(lesson);

        // Log the deletion
        logService.log("Lesson", "DELETE",
                String.format("Deleted %s lesson: %s",
                        lesson.getType(), lesson.getTitle()),
                lessonData, null);
    }

    private Lesson findLessonById(UUID id) {
        return lessonRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Lesson not found with id: " + id));
    }

    private Section findSectionById(UUID id) {
        return sectionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Section not found with id: " + id));
    }

    private void validateLessonRequest(LessonRequest request) {
        // Common validations
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new ValidationException("Lesson title cannot be empty");
        }

        if (request.getTitle().length() > 255) {
            throw new ValidationException("Lesson title cannot exceed 255 characters");
        }

        if (request.getOrderIndex() < 0) {
            throw new ValidationException("Order index cannot be negative");
        }

        // Type-specific validations
        switch (request.getType()) {
            case VIDEO:
                break;

            case READING:
                if (request.getContent() == null || request.getContent().trim().isEmpty()) {
                    throw new ValidationException("Content is required for reading lessons");
                }
                break;

            case QUIZ:
                if (request.getPassPoint() == null || request.getTotalPoint() == null || request.getDuration() == null) {
                    throw new ValidationException("Pass point, total point, and duration are required for quiz lessons");
                }

                if (request.getPassPoint() < 0 || request.getTotalPoint() < 0) {
                    throw new ValidationException("Points cannot be negative");
                }

                if (request.getPassPoint() > request.getTotalPoint()) {
                    throw new ValidationException("Pass point cannot be greater than total point");
                }

                if (request.getDuration() <= 0) {
                    throw new ValidationException("Duration must be positive");
                }
                break;

            default:
                throw new ValidationException("Unsupported lesson type: " + request.getType());
        }
    }

    /**
     * Validates that the order index is appropriate for the section
     *
     * @param orderIndex the requested order index
     * @param section    the section where the lesson belongs
     * @param lessonId   the ID of the lesson being updated (null for creation)
     */
    private void validateOrderIndex(int orderIndex, Section section, UUID lessonId) {
        List<Lesson> existingLessons = lessonRepository.findBySectionOrderByOrderIndex(section);

        // For updates, exclude the current lesson from duplicate check
        if (lessonId != null) {
            existingLessons = existingLessons.stream()
                    .filter(lesson -> !lesson.getId().equals(lessonId))
                    .collect(Collectors.toList());
        }

        // Check for duplicate order index
        boolean orderIndexExists = existingLessons.stream()
                .anyMatch(lesson -> lesson.getOrderIndex() == orderIndex);

        if (orderIndexExists) {
            throw new ValidationException(
                    String.format("A lesson with order index %d already exists in section '%s'",
                            orderIndex, section.getTitle()));
        }

        // Calculate the expected maximum order index
        int maxAllowedIndex = existingLessons.size();
        if (lessonId != null) {
            // When updating, we can use the same index or one more than the current size
            maxAllowedIndex++;
        }

        // Ensure the order index is within valid range
        if (orderIndex > maxAllowedIndex) {
            throw new ValidationException(
                    String.format("Order index %d is too large. Maximum allowed is %d for section '%s'",
                            orderIndex, maxAllowedIndex, section.getTitle()));
        }
    }

    private Lesson createLessonByType(LessonRequest request, Section section, User author) {
        Lesson lesson = switch (request.getType()) {
            case VIDEO -> {
                Video video = Video.builder()
                        .title(request.getTitle())
                        .section(section)
                        .free(request.isFree())
                        .orderIndex(request.getOrderIndex())
                        .author(author)
                        .build();
                yield videoRepository.save(video);
            }
            case READING -> {
                Reading reading = Reading.builder()
                        .title(request.getTitle())
                        .section(section)
                        .free(request.isFree())
                        .orderIndex(request.getOrderIndex())
                        .author(author)
                        .content(request.getContent())
                        .build();
                yield readingRepository.save(reading);
            }
            case QUIZ -> {
                Quiz quiz = Quiz.builder()
                        .title(request.getTitle())
                        .section(section)
                        .free(request.isFree())
                        .orderIndex(request.getOrderIndex())
                        .author(author)
                        .passPoint(request.getPassPoint())
                        .totalPoint(request.getTotalPoint())
                        .duration(request.getDuration())
                        .build();
                yield quizRepository.save(quiz);
            }
            default -> throw new ValidationException("Unsupported lesson type: " + request.getType());
        };

        // Use section's helper method to maintain bidirectional relationship
        section.addLesson(lesson);

        return lesson;
    }

    private void updateLessonByType(Lesson lesson, LessonRequest request) {
        switch (lesson.getType()) {
            case VIDEO:
                if (lesson instanceof Video video) {
                    videoRepository.save(video);
                }
                break;

            case READING:
                if (lesson instanceof Reading reading) {
                    reading.setContent(request.getContent());
                    readingRepository.save(reading);
                }
                break;

            case QUIZ:
                if (lesson instanceof Quiz quiz) {
                    quiz.setPassPoint(request.getPassPoint());
                    quiz.setTotalPoint(request.getTotalPoint());
                    quiz.setDuration(request.getDuration());
                    quizRepository.save(quiz);
                }
                break;

            default:
                throw new ValidationException("Unsupported lesson type: " + lesson.getType());
        }
    }
}
