package com.elearning.models.dto;

import lombok.Data;

@Data
public class AddCourseToPathRequest {
    private Long courseId;
    private Integer stepOrder;
}
