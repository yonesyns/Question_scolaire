package com.gestetud.api;
import com.gestetud.api.dto.NotificationPreferenceInput;
import com.gestetud.model.NotificationPreference;
import com.gestetud.service.NotificationPreferenceService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/students/{studentId}/notification-preferences")
public class NotificationPreferenceController {
 private final NotificationPreferenceService service;
 public NotificationPreferenceController(NotificationPreferenceService service){this.service=service;}
 @GetMapping public NotificationPreference find(@PathVariable Long studentId){return service.find(studentId);}
 @PutMapping public NotificationPreference update(@PathVariable Long studentId,@Valid @RequestBody NotificationPreferenceInput input){return service.update(studentId,input);}
}
