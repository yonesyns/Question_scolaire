package com.gestetud.api;

import com.gestetud.api.dto.GradeInput;
import com.gestetud.model.*;
import com.gestetud.service.AttendanceService;
import com.gestetud.repo.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping
public class GradeController extends ApiSupport {
    private final StudentRepository students; private final ExamRepository exams; private final GradeRepository grades;
    private final RegistrationRepository registrations; private final AttendanceService attendanceService;
    public GradeController(StudentRepository students, ExamRepository exams, GradeRepository grades, RegistrationRepository registrations, AttendanceService attendanceService) {
        this.students = students; this.exams = exams; this.grades = grades; this.registrations = registrations; this.attendanceService = attendanceService;
    }

    @PostMapping("/grades") @ResponseStatus(HttpStatus.CREATED)
    public Grade create(@Valid @RequestBody GradeInput input) {
        Exam exam = require(exams.findById(input.examId()), "Examen");
        Student student = require(students.findById(input.studentId()), "Étudiant");
        boolean registered = registrations.findByStudentId(student.id).stream()
                .anyMatch(r -> r.course.id.equals(exam.course.id) && r.status.equals("ACTIVE"));
        if (!registered) throw new ResponseStatusException(HttpStatus.CONFLICT, "L’étudiant n’est pas inscrit au module de cet examen");
        if (Boolean.TRUE.equals(exam.finalExam)) attendanceService.requireFinalExamEligibility(student.id, exam.course.id);
        return grades.save(new Grade(exam, student, input.value()));
    }

    @GetMapping("/students/{id}/grades")
    public List<Grade> findByStudent(@PathVariable Long id) {
        require(students.findById(id), "Étudiant");
        return grades.findByStudentId(id);
    }
}
