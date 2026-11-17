package com.gestetud.notification;
import org.springframework.stereotype.Component;
@Component
public class TwilioSmsSender implements NotificationChannelSender {
 private final TwilioClient client;
 public TwilioSmsSender(TwilioClient client){this.client=client;}
 public NotificationChannel channel(){return NotificationChannel.SMS;}
 public void send(String recipient,String subject,String body)throws Exception{
   client.sendSms(recipient,body);
 }
}
