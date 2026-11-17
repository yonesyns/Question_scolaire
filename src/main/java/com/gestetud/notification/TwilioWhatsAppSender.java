package com.gestetud.notification;
import org.springframework.stereotype.Component;
@Component
public class TwilioWhatsAppSender implements NotificationChannelSender {
 private final TwilioClient client;
 public TwilioWhatsAppSender(TwilioClient client){this.client=client;}
 public NotificationChannel channel(){return NotificationChannel.WHATSAPP;}
 public void send(String recipient,String subject,String body)throws Exception{
   client.sendWhatsApp(recipient,body);
 }
}
