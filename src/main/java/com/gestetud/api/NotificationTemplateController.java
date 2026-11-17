package com.gestetud.api;
import com.gestetud.api.dto.NotificationTemplateInput;
import com.gestetud.model.NotificationTemplate;
import com.gestetud.notification.*;
import com.gestetud.repo.NotificationTemplateRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
@RestController @RequestMapping("/notification-templates")
public class NotificationTemplateController {
 private final NotificationTemplateRepository templates;
 public NotificationTemplateController(NotificationTemplateRepository templates){this.templates=templates;}
 @GetMapping public List<NotificationTemplate> findAll(){return templates.findAll();}
 @PutMapping("/{eventType}/{channel}")
 public NotificationTemplate update(@PathVariable NotificationEventType eventType,@PathVariable NotificationChannel channel,@Valid @RequestBody NotificationTemplateInput input){
   NotificationTemplate t=templates.findByEventTypeAndChannel(eventType,channel).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Modèle de notification introuvable"));
   t.subject=input.subject();t.body=input.body();return templates.save(t);
 }
}
