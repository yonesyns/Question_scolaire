package com.gestetud;

import com.gestetud.model.NotificationTemplate;
import com.gestetud.notification.*;
import com.gestetud.repo.NotificationTemplateRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class NotificationTemplateInitializer implements CommandLineRunner {
    private final NotificationTemplateRepository templates;
    public NotificationTemplateInitializer(NotificationTemplateRepository templates){this.templates=templates;}
    @Override @Transactional
    public void run(String... args){
        add(NotificationEventType.REGISTRATION_ACCEPTED,"Inscription confirmée",
                "Bonjour {{studentName}}, votre inscription au module {{courseCode}} - {{courseTitle}} est acceptée.");
        add(NotificationEventType.SCHEDULE_CHANGED,"Changement d'emploi du temps",
                "Bonjour {{studentName}}, l'horaire de {{activityTitle}} ({{courseCode}}) a changé : {{oldDate}} → {{newDate}}.");
        add(NotificationEventType.GRADES_PUBLISHED,"Notes publiées",
                "Bonjour {{studentName}}, vos notes pour {{courseCode}} sont publiées. Moyenne : {{average}}/20.");
    }
    private void add(NotificationEventType event,String subject,String body){
        for(NotificationChannel channel:NotificationChannel.values())
            if(templates.findByEventTypeAndChannel(event,channel).isEmpty())
                templates.save(new NotificationTemplate(event,channel,channel==NotificationChannel.EMAIL?subject:null,body));
    }
}
