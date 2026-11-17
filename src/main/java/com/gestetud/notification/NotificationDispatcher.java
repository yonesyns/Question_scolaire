package com.gestetud.notification;

import com.gestetud.model.*;
import com.gestetud.repo.*;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class NotificationDispatcher {
    private final StudentRepository students; private final NotificationPreferenceRepository preferences;
    private final NotificationTemplateRepository templates; private final NotificationDeliveryRepository deliveries;
    private final Map<NotificationChannel,NotificationChannelSender> senders;
    public NotificationDispatcher(StudentRepository students,NotificationPreferenceRepository preferences,
                                  NotificationTemplateRepository templates,NotificationDeliveryRepository deliveries,
                                  List<NotificationChannelSender> senders){
        this.students=students;this.preferences=preferences;this.templates=templates;this.deliveries=deliveries;
        Map<NotificationChannel,NotificationChannelSender> byChannel=new EnumMap<>(NotificationChannel.class);
        senders.forEach(s->byChannel.put(s.channel(),s));this.senders=Map.copyOf(byChannel);
    }

    public void dispatch(NotificationRequested request){
        Student student=students.findById(request.studentId()).orElse(null);if(student==null)return;
        NotificationPreference preference=preferences.findByStudentId(student.id).orElseGet(()->new NotificationPreference(student));
        Map<String,String> values=new HashMap<>(request.values());values.put("studentName",student.name);
        for(NotificationChannel channel:NotificationChannel.values()){
            if(!enabled(preference,channel))continue;
            String recipient=recipient(student,preference,channel);
            NotificationTemplate template=templates.findByEventTypeAndChannel(request.eventType(),channel).orElse(null);
            String subject=template==null?null:render(template.subject,values);
            String body=template==null?null:render(template.body,values);
            String error=recipient==null?"Aucun destinataire configuré pour "+channel:(template==null?"Aucun modèle pour "+request.eventType()+"/"+channel:null);
            NotificationDelivery delivery=new NotificationDelivery(student,request.eventType(),channel,
                    recipient==null?"":recipient,subject,body==null?"":body);
            if(error!=null){delivery.status=DeliveryStatus.FAILED;delivery.errorMessage=error;deliveries.save(delivery);continue;}
            delivery=deliveries.save(delivery);
            try{
                NotificationChannelSender sender=senders.get(channel);
                if(sender==null)throw new IllegalStateException("Aucun fournisseur "+channel+" n'est configuré");
                sender.send(recipient,subject,body);
                delivery.status=DeliveryStatus.SENT;delivery.sentAt=LocalDateTime.now();delivery.errorMessage=null;
            }catch(Exception ex){
                delivery.status=DeliveryStatus.FAILED;
                String message=ex.getMessage()==null?ex.getClass().getSimpleName():ex.getMessage();
                delivery.errorMessage=message.substring(0,Math.min(1000,message.length()));
            }
            deliveries.save(delivery);
        }
    }

    private boolean enabled(NotificationPreference p,NotificationChannel c){
        return switch(c){case EMAIL->p.emailEnabled;case SMS->p.smsEnabled;case WHATSAPP->p.whatsappEnabled;};
    }
    private String recipient(Student s,NotificationPreference p,NotificationChannel c){
        return switch(c){case EMAIL->s.email;case SMS,WHATSAPP->p.phoneNumber==null||p.phoneNumber.isBlank()?null:p.phoneNumber;};
    }
    private String render(String text,Map<String,String> values){
        if(text==null)return "";
        String result=text;
        for(var entry:values.entrySet())result=result.replace("{{"+entry.getKey()+"}}",entry.getValue()==null?"":entry.getValue());
        return result;
    }
}
