package com.gestetud.api.dto;
import jakarta.validation.constraints.NotNull;
public record RegistrationInput(@NotNull Long studentId, @NotNull Long courseId) {}
