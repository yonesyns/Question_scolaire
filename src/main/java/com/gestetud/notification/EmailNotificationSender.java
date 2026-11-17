package com.gestetud.notification;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix="spring.mail",name="host")
public class EmailNotificationSender implements NotificationChannelSender {
    private final JavaMailSender mailSender;
    private final String from;
    public EmailNotificationSender(JavaMailSender mailSender,@Value("${spring.mail.username:}") String from){this.mailSender=mailSender;this.from=from;}
    @Override public NotificationChannel channel(){return NotificationChannel.EMAIL;}
    @Override public void send(String recipient,String subject,String body){
        SimpleMailMessage message=new SimpleMailMessage();message.setTo(recipient);
        if(!from.isBlank())message.setFrom(from);
        message.setSubject(subject==null||subject.isBlank()?"Notification Gestetud":subject);message.setText(body);
        mailSender.send(message);
    }
}
