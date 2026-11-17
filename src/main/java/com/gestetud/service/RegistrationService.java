package com.gestetud.service;

import com.gestetud.model.*;
import com.gestetud.api.dto.CourseResult;
import com.gestetud.repo.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.time.LocalDate;

@Service
public class RegistrationService {
    private final StudentRepository students;
    private final CourseRepository courses;
    private final RegistrationRepository registrations;
    private final CoursePrerequisiteRepository prerequisites;
    private final GradeRepository grades;
    private final CourseResultService courseResults;

    public RegistrationService(StudentRepository students, CourseRepository courses,
                               RegistrationRepository registrations, CoursePrerequisiteRepository prerequisites,
                               GradeRepository grades, CourseResultService courseResults) {
        this.students = students; this.courses = courses; this.registrations = registrations;
        this.prerequisites = prerequisites; this.grades = grades; this.courseResults = courseResults;
    }

    @Transactional
    public Registration register(Long studentId, Long courseId) {
        Student student = students.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Étudiant introuvable"));
        Course course = courses.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Module introuvable"));

        if (registrations.existsByStudentIdAndCourseId(student.id, course.id))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Étudiant déjà inscrit à ce module");

        validatePrerequisites(student, course);

        if (registrations.countByCourseIdAndStatus(course.id, "ACTIVE") >= course.capacity)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Capacité du module atteinte");

        return registrations.save(new Registration(student, course));
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
