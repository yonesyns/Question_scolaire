package com.gestetud.api;

import com.gestetud.api.dto.EvaluationRuleInput;
import com.gestetud.model.CourseEvaluationRule;
import com.gestetud.service.EvaluationRuleService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/courses/{courseId}/evaluation-rules")
public class CourseEvaluationRuleController {
    private final EvaluationRuleService service;
    public CourseEvaluationRuleController(EvaluationRuleService service){this.service=service;}
    @GetMapping public List<CourseEvaluationRule> find(@PathVariable Long courseId){return service.find(courseId);}
    @PutMapping public List<CourseEvaluationRule> replace(@PathVariable Long courseId,@Valid @RequestBody List<@Valid EvaluationRuleInput> inputs){return service.replace(courseId,inputs);}
}
