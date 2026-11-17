package com.gestetud.notification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

@Component
public class TwilioClient {
    private final String accountSid,authToken,smsFrom,whatsappFrom;
    private final HttpClient client=HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    public TwilioClient(@Value("${TWILIO_ACCOUNT_SID:}") String accountSid,
                        @Value("${TWILIO_AUTH_TOKEN:}") String authToken,
                        @Value("${TWILIO_SMS_FROM:}") String smsFrom,
                        @Value("${TWILIO_WHATSAPP_FROM:}") String whatsappFrom){
        this.accountSid=accountSid;this.authToken=authToken;this.smsFrom=smsFrom;this.whatsappFrom=whatsappFrom;
    }
    public void sendSms(String to,String body)throws Exception {send(to,smsFrom,body,false);}
    public void sendWhatsApp(String to,String body)throws Exception {
        if(whatsappFrom==null||whatsappFrom.isBlank())throw new IllegalStateException("TWILIO_WHATSAPP_FROM n'est pas configuré");
        send("whatsapp:"+to,whatsappFrom.startsWith("whatsapp:")?whatsappFrom:"whatsapp:"+whatsappFrom,body,true);
    }
    private void send(String to,String from,String body,boolean whatsapp)throws Exception {
        if(accountSid.isBlank()||authToken.isBlank())throw new IllegalStateException("TWILIO_ACCOUNT_SID et TWILIO_AUTH_TOKEN ne sont pas configurés");
        if(from==null||from.isBlank())throw new IllegalStateException(whatsapp?"TWILIO_WHATSAPP_FROM n'est pas configuré":"TWILIO_SMS_FROM n'est pas configuré");
        String form="To="+encode(to)+"&From="+encode(from)+"&Body="+encode(body);
        String path="https://api.twilio.com/2010-04-01/Accounts/"+accountSid+"/Messages.json";
        String auth=Base64.getEncoder().encodeToString((accountSid+":"+authToken).getBytes(StandardCharsets.UTF_8));
        HttpRequest request=HttpRequest.newBuilder(URI.create(path)).timeout(Duration.ofSeconds(20))
                .header("Authorization","Basic "+auth).header("Content-Type","application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(form)).build();
        HttpResponse<String> response=client.send(request,HttpResponse.BodyHandlers.ofString());
        if(response.statusCode()<200||response.statusCode()>=300)
            throw new IllegalStateException("Twilio a renvoyé HTTP "+response.statusCode()+": "+response.body().substring(0,Math.min(500,response.body().length())));
    }
    private String encode(String s){return URLEncoder.encode(s,StandardCharsets.UTF_8);}
}
