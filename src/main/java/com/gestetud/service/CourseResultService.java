package com.gestetud.service;

import com.gestetud.api.dto.CourseResult;
import com.gestetud.model.*;
import com.gestetud.repo.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
import java.time.LocalDate;

@Service
public class CourseResultService {
    private final StudentRepository students; private final CourseRepository courses; private final GradeRepository grades;
    private final CourseEvaluationRuleRepository rules; private final AttendanceService attendanceService; private final ResultPublicationRepository publications;
    public CourseResultService(StudentRepository students,CourseRepository courses,GradeRepository grades,CourseEvaluationRuleRepository rules,AttendanceService attendanceService,ResultPublicationRepository publications){
        this.students=students;this.courses=courses;this.grades=grades;this.rules=rules;this.attendanceService=attendanceService;this.publications=publications;
    }
    @Transactional(readOnly=true)
    public CourseResult calculate(Long studentId,Long courseId){
        Student student=students.findById(studentId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Étudiant introuvable"));
        Course course=courses.findById(courseId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Module introuvable"));
        List<CourseEvaluationRule> courseRules=rules.findByCourseId(courseId);
        if(courseRules.isEmpty())throw new ResponseStatusException(HttpStatus.CONFLICT,"Aucune formule de notation n'est configurée pour ce module");
        Map<String,List<Double>> values=new HashMap<>();
        for(Grade grade:grades.findByStudentId(studentId)){
            if(grade.exam.course.id.equals(courseId)&&grade.exam.evaluationType!=null&&!grade.exam.date.isAfter(LocalDate.now()))
                values.computeIfAbsent(grade.exam.evaluationType.code,k->new ArrayList<>()).add(grade.value);
        }
        Map<String,Double> means=new LinkedHashMap<>(); double weightedSum=0,coveredWeight=0;
        for(CourseEvaluationRule rule:courseRules){
            String code=rule.evaluationType.code; List<Double> marks=values.get(code);
            if(marks==null||marks.isEmpty())continue;
            double mean=marks.stream().mapToDouble(Double::doubleValue).average().orElseThrow();
            means.put(code,round(mean)); weightedSum+=mean*rule.weight/100.0; coveredWeight+=rule.weight;
        }
        boolean complete=coveredWeight>=99.9999;
        Double provisional=coveredWeight==0?null:round(weightedSum*100.0/coveredWeight);
        var eligibility=attendanceService.eligibility(student.id,course.id);
        Double finalAverage=complete&&eligibility.eligible()?round(weightedSum):null;
        boolean published=publications.findByStudentIdAndCourseId(student.id,course.id).isPresent();
        String publicationStatus=!eligibility.eligible()?"INELIGIBLE":(!complete?"INCOMPLETE":(published?"PUBLISHED":"READY"));
        return new CourseResult(student.id,course.id,course.code,course.title,finalAverage,provisional,round(coveredWeight),complete,eligibility.eligible(),eligibility.absencePercentage(),published,publicationStatus,means);
    }
    private double round(double value){return Math.round(value*100.0)/100.0;}
}
