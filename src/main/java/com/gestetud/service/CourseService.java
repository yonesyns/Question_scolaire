package com.gestetud.service;

import com.gestetud.model.*;
import com.gestetud.repo.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class CourseService {
    private final CourseRepository courses; private final EvaluationTypeRepository types; private final CourseEvaluationRuleRepository rules;
    public CourseService(CourseRepository courses,EvaluationTypeRepository types,CourseEvaluationRuleRepository rules){this.courses=courses;this.types=types;this.rules=rules;}
    @Transactional
    public Course create(String code,String title,Integer capacity){
        Course course=courses.save(new Course(code,title,capacity));
        EvaluationType continuous=types.findByCode("CONTINUOUS").orElseThrow();
        EvaluationType exam=types.findByCode("EXAM").orElseThrow();
        EvaluationType project=types.findByCode("PROJECT").orElseThrow();
        rules.saveAll(List.of(new CourseEvaluationRule(course,continuous,30.0),new CourseEvaluationRule(course,exam,50.0),new CourseEvaluationRule(course,project,20.0)));
        return course;
    }
}
