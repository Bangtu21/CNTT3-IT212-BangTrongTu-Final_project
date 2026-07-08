package com.elearning.controllers;

import com.elearning.advice.ApiResponse;
import com.elearning.models.dto.AddCourseToPathRequest;
import com.elearning.models.services.LearningPathService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/learning-path")
@RequiredArgsConstructor
public class AdminController {

    private final LearningPathService learningPathService;

    @PostMapping("/{learningPathId}/courses")
    public ResponseEntity<ApiResponse<Void>> addCourseToLearningPath(
            @PathVariable Long learningPathId,
            @RequestBody AddCourseToPathRequest request) {
        learningPathService.addCourseToLearningPath(learningPathId, request);
        return ResponseEntity.ok(ApiResponse.success(null, "Course added to learning path successfully"));
    }
}
