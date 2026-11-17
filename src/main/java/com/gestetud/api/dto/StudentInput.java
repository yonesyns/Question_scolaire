package com.gestetud.api.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
public record StudentInput(@NotBlank String name, @Email @NotBlank String email, @NotBlank String program) {}
