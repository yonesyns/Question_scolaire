package com.gestetud.api.dto;
import jakarta.validation.constraints.NotBlank;
public record EvaluationTypeInput(@NotBlank String code,@NotBlank String label) {}
