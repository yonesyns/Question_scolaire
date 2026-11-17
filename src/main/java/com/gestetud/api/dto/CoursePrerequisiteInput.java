package com.gestetud.api.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record CoursePrerequisiteInput(
        @NotNull Long prerequisiteCourseId,
        Long equivalentCourseId,
        @DecimalMin("0.0") @DecimalMax("20.0") Double minimumGrade) {}
