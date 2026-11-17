package com.gestetud.api;
import com.gestetud.model.NotificationDelivery;
import com.gestetud.repo.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
@RestController @RequestMapping("/students/{studentId}/notifications")
public class NotificationHistoryController {
 private final StudentRepository students;private final NotificationDeliveryRepository deliveries;
 public NotificationHistoryController(StudentRepository students,NotificationDeliveryRepository deliveries){this.students=students;this.deliveries=deliveries;}
 @GetMapping public List<NotificationDelivery> find(@PathVariable Long studentId){
   if(!students.existsById(studentId))throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Étudiant introuvable");
   return deliveries.findByStudentIdOrderByCreatedAtDesc(studentId);
 }
}
