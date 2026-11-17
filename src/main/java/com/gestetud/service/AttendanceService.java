package com.gestetud.service;

import com.gestetud.api.dto.ExamEligibility;
import com.gestetud.model.*;
import com.gestetud.repo.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.util.List;

@Service
public class AttendanceService {
    private final CourseRepository courses; private final StudentRepository students;
    private final RegistrationRepository registrations; private final CourseSessionRepository sessions;
    private final AttendanceRepository attendances;
    public AttendanceService(CourseRepository courses,StudentRepository students,RegistrationRepository registrations,
                             CourseSessionRepository sessions,AttendanceRepository attendances) {
        this.courses=courses;this.students=students;this.registrations=registrations;this.sessions=sessions;this.attendances=attendances;
    }

    @Transactional
    public CourseSession createSession(Long courseId,LocalDate date,String title) {
        Course course=courses.findById(courseId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Module introuvable"));
        return sessions.save(new CourseSession(course,date,title));
    }

    @Transactional(readOnly=true)
    public List<CourseSession> findSessions(Long courseId) {
        if(!courses.existsById(courseId))throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Module introuvable");
        return sessions.findByCourseIdOrderByDate(courseId);
    }

    @Transactional
    public Attendance mark(Long sessionId,Long studentId,AttendanceStatus status) {
        CourseSession session=sessions.findById(sessionId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Séance introuvable"));
        Student student=students.findById(studentId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Étudiant introuvable"));
        boolean registered=registrations.findByStudentId(studentId).stream()
                .anyMatch(r->r.course.id.equals(session.course.id)&&"ACTIVE".equals(r.status));
        if(!registered)throw new ResponseStatusException(HttpStatus.CONFLICT,"L'étudiant n'est pas inscrit à ce module");
        Attendance attendance=attendances.findBySessionIdAndStudentId(sessionId,studentId).orElse(new Attendance(session,student,status));
        attendance.status=status;
        return attendances.save(attendance);
    }

    @Transactional(readOnly=true)
    public List<Attendance> findAttendance(Long sessionId) {
        if(!sessions.existsById(sessionId))throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Séance introuvable");
        return attendances.findBySessionId(sessionId);
    }

    @Transactional(readOnly=true)
    public ExamEligibility eligibility(Long studentId,Long courseId) {
        Student student=students.findById(studentId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Étudiant introuvable"));
        Course course=courses.findById(courseId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Module introuvable"));
        List<CourseSession> held=sessions.findByCourseIdOrderByDate(courseId).stream()
                .filter(s->!s.date.isAfter(LocalDate.now())).toList();
        int absences=(int)attendances.findByStudentId(studentId).stream()
                .filter(a->a.status==AttendanceStatus.ABSENT && !a.session.date.isAfter(LocalDate.now()) && a.session.course.id.equals(courseId)).count();
        double percentage=held.isEmpty()?0.0:absences*100.0/held.size();
        boolean eligible=held.isEmpty() || absences*100L<=held.size()*20L;
        String reason=eligible?"Absences dans la limite autorisée":"Plus de 20 % d'absences : l'étudiant ne peut pas passer l'examen final";
        return new ExamEligibility(student.id,course.id,held.size(),absences,Math.round(percentage*100.0)/100.0,eligible,reason);
    }

    @Transactional(readOnly=true)
    public void requireFinalExamEligibility(Long studentId,Long courseId) {
        ExamEligibility eligibility=eligibility(studentId,courseId);
        if(!eligibility.eligible())throw new ResponseStatusException(HttpStatus.CONFLICT,eligibility.reason());
    }
}
