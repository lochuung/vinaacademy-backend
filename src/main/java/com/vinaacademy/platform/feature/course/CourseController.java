package com.vinaacademy.platform.feature.course;

import com.vinaacademy.platform.feature.user.auth.annotation.HasAnyRole;
import com.vinaacademy.platform.feature.user.constant.AuthConstants;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/courses")
@Slf4j
@SecurityRequirement(name = "bearerAuth")
public class CourseController {

    @HasAnyRole({AuthConstants.ADMIN_ROLE, AuthConstants.INSTRUCTOR_ROLE})
    @PostMapping
    public void createCourse() {
        // Only ADMIN and INSTRUCTOR can create courses
        log.debug("Course created");
    }

    @HasAnyRole({AuthConstants.ADMIN_ROLE})
    @DeleteMapping("/{id}")
    public void deleteCourse(@PathVariable String id) {
        // Only ADMIN can delete courses
        log.debug("Course deleted");
    }

    @HasAnyRole({AuthConstants.INSTRUCTOR_ROLE})
    @PutMapping("/{id}")
    public void updateCourse(@PathVariable String id) {
        // Only INSTRUCTOR can update their courses
        log.debug("Course updated");
    }
}
