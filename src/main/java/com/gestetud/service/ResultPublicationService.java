package com.gestetud.service;

import com.gestetud.api.dto.CourseResult;
import com.gestetud.model.*;
import com.gestetud.notification.*;
import com.gestetud.repo.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;

@Service
public class ResultPublicationService {
    private final CourseRepository courses; private final RegistrationRepository registrations;
    private final StudentRepository students; private final ResultPublicationRepository publications;
    private final CourseResultService results; private final ApplicationEventPublisher events;
    public ResultPublicationService(CourseRepository courses,RegistrationRepository registrations,StudentRepository students,
                                    ResultPublicationRepository publications,CourseResultService results,ApplicationEventPublisher events){
        this.courses=courses;this.registrations=registrations;this.students=students;this.publications=publications;this.results=results;this.events=events;
    }
    @Transactional
    public List<ResultPublication> publishCourse(Long courseId){
        Course course=courses.findById(courseId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Module introuvable"));
        List<ResultPublication> result=new ArrayList<>();
        for(Registration registration:registrations.findByCourseIdAndStatusOrderByIdAsc(courseId,Registration.ACTIVE)){
            Student student=registration.student;
            CourseResult calculated=results.calculate(student.id,course.id);
            if(calculated.finalAverage()==null)continue;
            ResultPublication publication=publications.findByStudentIdAndCourseId(student.id,course.id)
                    .orElseGet(()->new ResultPublication(student,course,calculated.finalAverage()));
            publication.average=calculated.finalAverage();publication.publishedAt=java.time.LocalDateTime.now();
            result.add(publications.save(publication));
            events.publishEvent(new NotificationRequested(student.id,NotificationEventType.GRADES_PUBLISHED,
                    Map.of("courseCode",course.code,"courseTitle",course.title,"average",String.valueOf(calculated.finalAverage()))));
        }
        return result;
    }
}
