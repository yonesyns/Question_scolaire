package com.gestetud.api.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
public record CourseSessionInput(@NotNull LocalDate date,@NotBlank String title){}
