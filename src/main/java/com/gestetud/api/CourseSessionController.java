package com.gestetud.api;

import com.gestetud.api.dto.CourseSessionInput;
import com.gestetud.model.CourseSession;
import com.gestetud.service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/courses/{courseId}/sessions")
public class CourseSessionController {
    private final AttendanceService service;
    public CourseSessionController(AttendanceService service){this.service=service;}
    @GetMapping public List<CourseSession> find(@PathVariable Long courseId){return service.findSessions(courseId);}
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public CourseSession create(@PathVariable Long courseId,@Valid @RequestBody CourseSessionInput input){
        return service.createSession(courseId,input.date(),input.title());
    }
}
