package com.gestetud.api;

import com.gestetud.api.dto.AttendanceInput;
import com.gestetud.api.dto.ExamEligibility;
import com.gestetud.model.Attendance;
import com.gestetud.service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping
public class AttendanceController {
    private final AttendanceService service;
    public AttendanceController(AttendanceService service){this.service=service;}
    @PutMapping("/sessions/{sessionId}/attendance/{studentId}")
    public Attendance mark(@PathVariable Long sessionId,@PathVariable Long studentId,@Valid @RequestBody AttendanceInput input){
        return service.mark(sessionId,studentId,input.status());
    }
    @GetMapping("/sessions/{sessionId}/attendance")
    public List<Attendance> find(@PathVariable Long sessionId){return service.findAttendance(sessionId);}
    @GetMapping("/students/{studentId}/courses/{courseId}/exam-eligibility")
    public ExamEligibility eligibility(@PathVariable Long studentId,@PathVariable Long courseId){
        return service.eligibility(studentId,courseId);
    }
}
