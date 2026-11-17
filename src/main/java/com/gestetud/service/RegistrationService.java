package com.gestetud.service;

import com.gestetud.model.*;
import com.gestetud.api.dto.CourseResult;
import com.gestetud.api.dto.CancellationResult;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import com.gestetud.repo.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class RegistrationService {
    private final StudentRepository students;
    private final CourseRepository courses;
    private final RegistrationRepository registrations;
    private final CoursePrerequisiteRepository prerequisites;
    private final GradeRepository grades;
    private final CourseResultService courseResults;
    private final EntityManager entityManager;

    public RegistrationService(StudentRepository students, CourseRepository courses,
                               RegistrationRepository registrations, CoursePrerequisiteRepository prerequisites,
                               GradeRepository grades, CourseResultService courseResults, EntityManager entityManager) {
        this.students = students; this.courses = courses; this.registrations = registrations;
        this.prerequisites = prerequisites; this.grades = grades; this.courseResults = courseResults; this.entityManager = entityManager;
    }

    @Transactional
    public Registration register(Long studentId, Long courseId) {
        Student student = students.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Étudiant introuvable"));
        Course course = courses.findByIdForUpdate(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Module introuvable"));

        Optional<Registration> existing = registrations.findByStudentIdAndCourseId(student.id, course.id);
        if (existing.isPresent() && !Registration.CANCELLED.equals(existing.get().status))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Étudiant déjà inscrit ou déjà en liste d'attente");

        validatePrerequisites(student, course);
        long activeCount = registrations.countByCourseIdAndStatus(course.id, Registration.ACTIVE);
        Registration registration = existing.orElseGet(() -> new Registration(student, course));
        registration.status = activeCount < course.capacity ? Registration.ACTIVE : Registration.WAITING_LIST;
        registration.createdAt = LocalDateTime.now();
        return registrations.save(registration);
    }

    @Transactional
    public CancellationResult cancel(Long registrationId) {
        Registration initial = registrations.findById(registrationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inscription introuvable"));
        Course course = courses.findByIdForUpdate(initial.course.id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Module introuvable"));
        entityManager.refresh(initial, LockModeType.PESSIMISTIC_WRITE);
        Registration registration = initial;
        if (Registration.CANCELLED.equals(registration.status))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cette inscription est déjà annulée");

        boolean releasesSeat = Registration.ACTIVE.equals(registration.status);
        registration.status = Registration.CANCELLED;
        registrations.save(registration);

        Registration promoted = null;
        if (releasesSeat) {
            promoted = registrations.findByCourseIdAndStatusOrderByCreatedAtAscIdAsc(course.id, Registration.WAITING_LIST)
                    .stream().findFirst().orElse(null);
            if (promoted != null) {
                promoted.status = Registration.ACTIVE;
                registrations.save(promoted);
            }
        }
        return new CancellationResult(registration, promoted,
                promoted == null ? "Inscription annulée" : "Inscription annulée ; le premier étudiant en attente a été promu");
    }

    @Transactional(readOnly = true)
    public List<Registration> findWaitingList(Long courseId) {
        if (!courses.existsById(courseId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Module introuvable");
        return registrations.findByCourseIdAndStatusOrderByCreatedAtAscIdAsc(courseId, Registration.WAITING_LIST);
    }

    private void validatePrerequisites(Student student, Course course) {
        for (CoursePrerequisite rule : prerequisites.findByCourseId(course.id)) {
            boolean hasRequiredGrade = hasPassingGrade(student.id, rule.prerequisiteCourse.id, rule.minimumGrade);
            boolean hasEquivalentGrade = rule.equivalentCourse != null
                    && hasPassingGrade(student.id, rule.equivalentCourse.id, rule.minimumGrade);
            if (hasRequiredGrade || hasEquivalentGrade) continue;

            boolean hasAnyHistory = hasAnyGrade(student.id, rule.prerequisiteCourse.id)
                    || (rule.equivalentCourse != null && hasAnyGrade(student.id, rule.equivalentCourse.id));
            String required = rule.prerequisiteCourse.code + " (note minimale : " + rule.minimumGrade + "/20)";
            if (rule.equivalentCourse != null) required += " ou son équivalence " + rule.equivalentCourse.code;

            if (!hasAnyHistory)
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Prérequis absent : validation de " + required + " requise avant l'inscription à " + course.code);
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Prérequis non validé : " + required + " n'a pas été validé ; inscription à " + course.code + " refusée");
        }
    }

    private boolean hasPassingGrade(Long studentId, Long courseId, Double minimumGrade) {
        CourseResult result = courseResults.calculate(studentId, courseId);
        return result.complete() && result.finalAverage() != null && result.finalAverage() >= minimumGrade;
    }

    private boolean hasAnyGrade(Long studentId, Long courseId) {
        return grades.findByStudentId(studentId).stream().anyMatch(g -> g.exam.course.id.equals(courseId) && !g.exam.date.isAfter(LocalDate.now()));
    }

    @Transactional(readOnly = true)
    public List<Registration> findByStudent(Long studentId) {
        if (!students.existsById(studentId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Étudiant introuvable");
        return registrations.findByStudentId(studentId);
    }
}
