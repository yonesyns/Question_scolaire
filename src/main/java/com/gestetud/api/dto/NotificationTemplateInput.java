package com.gestetud.api.dto;
import jakarta.validation.constraints.NotBlank;
public record NotificationTemplateInput(String subject,@NotBlank String body) {}
