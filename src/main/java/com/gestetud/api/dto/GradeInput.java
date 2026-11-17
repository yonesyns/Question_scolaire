package com.gestetud.api.dto;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
public record GradeInput(@NotNull Long examId, @NotNull Long studentId, @NotNull @DecimalMin("0.0") @DecimalMax("20.0") Double value) {}
