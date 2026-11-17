package com.gestetud.api.dto;
import jakarta.validation.constraints.NotNull;
public record NotificationPreferenceInput(@NotNull Boolean emailEnabled,@NotNull Boolean smsEnabled,@NotNull Boolean whatsappEnabled,String phoneNumber) {}
