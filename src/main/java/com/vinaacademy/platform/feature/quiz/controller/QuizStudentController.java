package com.vinaacademy.platform.feature.quiz.controller;

import com.vinaacademy.platform.feature.common.response.ApiResponse;
import com.vinaacademy.platform.feature.quiz.dto.QuizDto;
import com.vinaacademy.platform.feature.quiz.dto.QuizSubmissionRequest;
import com.vinaacademy.platform.feature.quiz.dto.QuizSubmissionResultDto;
import com.vinaacademy.platform.feature.quiz.service.QuizService;
import com.vinaacademy.platform.feature.user.auth.annotation.HasAnyRole;
import com.vinaacademy.platform.feature.user.constant.AuthConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/quiz")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Student Quiz", description = "Student quiz APIs")
public class QuizStudentController {
    private final QuizService quizService;
    
    @Operation(summary = "Get quiz for student")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved quiz",
                    content = @Content(schema = @Schema(implementation = QuizDto.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Quiz not found"
            )
    })
    @HasAnyRole({AuthConstants.STUDENT_ROLE})
    @GetMapping("/{id}")
    public ApiResponse<QuizDto> getQuiz(@PathVariable UUID id) {
        return ApiResponse.success(quizService.getQuizForStudent(id));
    }
    
    @Operation(summary = "Submit quiz answers")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Successfully submitted quiz",
                    content = @Content(schema = @Schema(implementation = QuizSubmissionResultDto.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid request"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Quiz not found"
            )
    })
    @HasAnyRole({AuthConstants.STUDENT_ROLE})
    @PostMapping("/submit")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<QuizSubmissionResultDto> submitQuiz(@RequestBody @Valid QuizSubmissionRequest request) {
        return ApiResponse.success(quizService.submitQuiz(request));
    }
    
    @Operation(summary = "Get latest quiz submission")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved latest submission",
                    content = @Content(schema = @Schema(implementation = QuizSubmissionResultDto.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "No submission found"
            )
    })
    @HasAnyRole({AuthConstants.STUDENT_ROLE})
    @GetMapping("/{quizId}/submission/latest")
    public ApiResponse<QuizSubmissionResultDto> getLatestSubmission(@PathVariable UUID quizId) {
        return ApiResponse.success(quizService.getLatestSubmission(quizId));
    }
    
    @Operation(summary = "Get all quiz submissions by current student")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved submission history",
                    content = @Content(schema = @Schema(implementation = QuizSubmissionResultDto.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "No submissions found"
            )
    })
    @HasAnyRole({AuthConstants.STUDENT_ROLE})
    @GetMapping("/{quizId}/submissions")
    public ApiResponse<List<QuizSubmissionResultDto>> getSubmissionHistory(@PathVariable UUID quizId) {
        return ApiResponse.success(quizService.getSubmissionHistory(quizId));
    }
}
