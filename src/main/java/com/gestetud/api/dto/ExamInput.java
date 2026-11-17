package com.gestetud.api.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
public record ExamInput(@NotNull Long courseId, @NotNull LocalDate date, @NotBlank String title, @NotBlank String evaluationTypeCode, Boolean finalExam) {}
