package com.elearning.controllers;

import com.elearning.advice.ApiResponse;
import com.elearning.models.services.LearningPathService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/student/courses")
@RequiredArgsConstructor
public class StudentController {

    private final LearningPathService learningPathService;

    @PostMapping("/{courseId}/complete")
    public ResponseEntity<ApiResponse<Void>> completeCourse(
            @PathVariable Long courseId,
            Principal principal) {
        String email = principal.getName();
        learningPathService.completeCourse(courseId, email);
        return ResponseEntity.ok(ApiResponse.success(null, "Course marked as completed successfully"));
    }

    @GetMapping("/{courseId}/access")
    public ResponseEntity<ApiResponse<Void>> accessCourse(
            @PathVariable Long courseId,
            Principal principal) {
        String email = principal.getName();
        learningPathService.accessCourse(courseId, email);
        return ResponseEntity.ok(ApiResponse.success(null, "Access allowed"));
    }
}
