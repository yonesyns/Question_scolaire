package com.gestetud.service;

import com.gestetud.model.Course;
import com.gestetud.model.CoursePrerequisite;
import com.gestetud.repo.CoursePrerequisiteRepository;
import com.gestetud.repo.CourseRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.HashSet;
import java.util.Set;

@Service
public class CoursePrerequisiteService {
    private final CourseRepository courses;
    private final CoursePrerequisiteRepository prerequisites;
    public CoursePrerequisiteService(CourseRepository courses, CoursePrerequisiteRepository prerequisites) {
        this.courses = courses; this.prerequisites = prerequisites;
    }

    @Transactional(readOnly = true)
    public java.util.List<CoursePrerequisite> findForCourse(Long courseId) {
        requireCourse(courseId);
        return prerequisites.findByCourseId(courseId);
    }

    @Transactional
    public CoursePrerequisite add(Long courseId, Long prerequisiteId, Long equivalentId, Double minimumGrade) {
        Course course = requireCourse(courseId);
        Course prerequisite = requireCourse(prerequisiteId);
        Course equivalent = equivalentId == null ? null : requireCourse(equivalentId);
        if (course.id.equals(prerequisite.id))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Un module ne peut pas être son propre prérequis");
        if (equivalent != null && (equivalent.id.equals(course.id) || equivalent.id.equals(prerequisite.id)))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le module équivalent doit être différent du module cible et du prérequis");
        if (prerequisites.findByCourseIdAndPrerequisiteCourseId(courseId, prerequisiteId).isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ce prérequis existe déjà pour ce module");
        if (dependsOn(prerequisiteId, courseId, new HashSet<>()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cette relation créerait une boucle de prérequis");
        return prerequisites.save(new CoursePrerequisite(course, prerequisite, equivalent,
                minimumGrade == null ? 10.0 : minimumGrade));
    }

    @Transactional
    public void remove(Long courseId, Long prerequisiteId) {
        CoursePrerequisite rule = prerequisites.findByCourseIdAndPrerequisiteCourseId(courseId, prerequisiteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prérequis introuvable pour ce module"));
        prerequisites.delete(rule);
    }

    private boolean dependsOn(Long fromCourseId, Long soughtCourseId, Set<Long> visited) {
        if (fromCourseId.equals(soughtCourseId)) return true;
        if (!visited.add(fromCourseId)) return false;
        for (CoursePrerequisite rule : prerequisites.findByCourseId(fromCourseId))
            if (dependsOn(rule.prerequisiteCourse.id, soughtCourseId, visited)) return true;
        return false;
    }

    private Course requireCourse(Long id) {
        return courses.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Module introuvable"));
    }
}
