package com.gestetud.api.dto;
import com.gestetud.model.AttendanceStatus;
import jakarta.validation.constraints.NotNull;
public record AttendanceInput(@NotNull AttendanceStatus status){}
