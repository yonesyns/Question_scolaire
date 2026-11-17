package com.gestetud.api;

import com.gestetud.api.dto.CourseResult;
import com.gestetud.service.CourseResultService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/students/{studentId}/courses/{courseId}/result")
public class CourseResultController {
    private final CourseResultService service;
    public CourseResultController(CourseResultService service){this.service=service;}
    @GetMapping public CourseResult calculate(@PathVariable Long studentId,@PathVariable Long courseId){return service.calculate(studentId,courseId);}
}
