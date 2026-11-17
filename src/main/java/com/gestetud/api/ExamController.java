package com.gestetud.api;

import com.gestetud.api.dto.ExamInput;
import com.gestetud.model.Exam;
import com.gestetud.repo.CourseRepository;
import com.gestetud.repo.EvaluationTypeRepository;
import com.gestetud.repo.ExamRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/exams")
public class ExamController extends ApiSupport {
    private final CourseRepository courses; private final ExamRepository exams; private final EvaluationTypeRepository types;
    public ExamController(CourseRepository courses, ExamRepository exams, EvaluationTypeRepository types) { this.courses=courses;this.exams=exams;this.types=types; }
    @GetMapping public List<Exam> findAll() { return exams.findAll(); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public Exam create(@Valid @RequestBody ExamInput input) {
        var course=require(courses.findById(input.courseId()),"Module");
        var type=require(types.findByCode(input.evaluationTypeCode()),"Type d'évaluation");
        return exams.save(new Exam(course,input.date(),input.title(),type,Boolean.TRUE.equals(input.finalExam())));
    }
}
