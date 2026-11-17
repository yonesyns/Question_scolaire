package com.gestetud.api;

import com.gestetud.api.dto.ExamInput;
import com.gestetud.model.*;
import com.gestetud.notification.*;
import com.gestetud.repo.*;
import jakarta.validation.Valid;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/exams")
public class ExamController extends ApiSupport {
    private final CourseRepository courses; private final ExamRepository exams; private final EvaluationTypeRepository types;
    private final RegistrationRepository registrations; private final ApplicationEventPublisher events;
    public ExamController(CourseRepository courses,ExamRepository exams,EvaluationTypeRepository types,
                          RegistrationRepository registrations,ApplicationEventPublisher events){
        this.courses=courses;this.exams=exams;this.types=types;this.registrations=registrations;this.events=events;
    }
    @GetMapping public List<Exam> findAll() { return exams.findAll(); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public Exam create(@Valid @RequestBody ExamInput input) {
        Course course=require(courses.findById(input.courseId()),"Module");
        EvaluationType type=require(types.findByCode(input.evaluationTypeCode()),"Type d'évaluation");
        return exams.save(new Exam(course,input.date(),input.title(),type,Boolean.TRUE.equals(input.finalExam())));
    }
    @PutMapping("/{id}") @Transactional
    public Exam update(@PathVariable Long id,@Valid @RequestBody ExamInput input){
        Exam exam=require(exams.findById(id),"Examen");Course oldCourse=exam.course;String oldTitle=exam.title;var oldDate=exam.date;
        Course course=require(courses.findById(input.courseId()),"Module");EvaluationType type=require(types.findByCode(input.evaluationTypeCode()),"Type d'évaluation");
        exam.course=course;exam.date=input.date();exam.title=input.title();exam.evaluationType=type;exam.finalExam=Boolean.TRUE.equals(input.finalExam());
        Exam saved=exams.save(exam);
        if(!oldDate.equals(exam.date)||!oldTitle.equals(exam.title)||!oldCourse.id.equals(course.id)){
            Set<Long> notified=new HashSet<>();
            for(Course affected:List.of(oldCourse,course)){
                for(Registration registration:registrations.findByCourseIdAndStatusOrderByIdAsc(affected.id,Registration.ACTIVE)){
                    if(notified.add(registration.student.id))events.publishEvent(new NotificationRequested(registration.student.id,NotificationEventType.SCHEDULE_CHANGED,
                            Map.of("courseCode",course.code,"activityTitle",exam.title,"oldDate",oldDate.toString(),"newDate",exam.date.toString())));
                }
            }
        }
        return saved;
    }
}
