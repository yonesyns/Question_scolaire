package com.gestetud.api;

import com.gestetud.api.dto.StudentInput;
import com.gestetud.model.Student;
import com.gestetud.repo.StudentRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController extends ApiSupport {
    private final StudentRepository students;
    public StudentController(StudentRepository students) { this.students = students; }

    @GetMapping public List<Student> findAll() { return students.findAll(); }
    @GetMapping("/{id}") public Student findById(@PathVariable Long id) { return require(students.findById(id), "Étudiant"); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public Student create(@Valid @RequestBody StudentInput input) { return students.save(new Student(input.name(), input.email(), input.program())); }
    @PutMapping("/{id}")
    public Student update(@PathVariable Long id, @Valid @RequestBody StudentInput input) {
        Student student = findById(id); student.name = input.name(); student.email = input.email(); student.program = input.program();
        return students.save(student);
    }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { students.delete(findById(id)); }
}
