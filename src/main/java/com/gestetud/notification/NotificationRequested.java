package com.gestetud.notification;
import java.util.Map;
public record NotificationRequested(Long studentId, NotificationEventType eventType, Map<String,String> values) {}
