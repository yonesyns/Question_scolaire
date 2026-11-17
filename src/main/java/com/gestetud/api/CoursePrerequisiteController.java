package com.gestetud.api;

import com.gestetud.api.dto.CoursePrerequisiteInput;
import com.gestetud.model.CoursePrerequisite;
import com.gestetud.service.CoursePrerequisiteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/courses/{courseId}/prerequisites")
public class CoursePrerequisiteController {
    private final CoursePrerequisiteService service;
    public CoursePrerequisiteController(CoursePrerequisiteService service) { this.service = service; }

    @GetMapping
    public List<CoursePrerequisite> findForCourse(@PathVariable Long courseId) { return service.findForCourse(courseId); }

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public CoursePrerequisite add(@PathVariable Long courseId, @Valid @RequestBody CoursePrerequisiteInput input) {
        return service.add(courseId, input.prerequisiteCourseId(), input.equivalentCourseId(), input.minimumGrade());
    }

    @DeleteMapping("/{prerequisiteCourseId}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable Long courseId, @PathVariable Long prerequisiteCourseId) {
        service.remove(courseId, prerequisiteCourseId);
    }
}
