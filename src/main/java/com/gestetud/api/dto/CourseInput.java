package com.gestetud.api.dto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
public record CourseInput(@NotBlank String code, @NotBlank String title, @Min(1) Integer capacity) {}
