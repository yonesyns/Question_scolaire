package com.gestetud.api.dto;
import com.gestetud.model.Registration;
public record CancellationResult(Registration cancelledRegistration,Registration promotedRegistration,String message) {}
