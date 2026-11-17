package com.gestetud.api;

import com.gestetud.api.dto.CourseInput;
import com.gestetud.model.Course;
import com.gestetud.repo.CourseRepository;
import com.gestetud.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseController extends ApiSupport {
    private final CourseRepository courses; private final CourseService courseService;
    public CourseController(CourseRepository courses, CourseService courseService) { this.courses = courses; this.courseService = courseService; }

    @GetMapping public List<Course> findAll() { return courses.findAll(); }
    @GetMapping("/{id}") public Course findById(@PathVariable Long id) { return require(courses.findById(id), "Module"); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public Course create(@Valid @RequestBody CourseInput input) { return courseService.create(input.code(), input.title(), input.capacity()); }
    @PutMapping("/{id}")
    public Course update(@PathVariable Long id, @Valid @RequestBody CourseInput input) {
        Course course = findById(id); course.code = input.code(); course.title = input.title(); course.capacity = input.capacity();
        return courses.save(course);
    }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { courses.delete(findById(id)); }
}
