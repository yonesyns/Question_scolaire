package com.gestetud.notification;
public interface NotificationChannelSender {
    NotificationChannel channel();
    void send(String recipient,String subject,String body) throws Exception;
}
