package com.gestetud.repo;

import com.gestetud.model.CoursePrerequisite;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CoursePrerequisiteRepository extends JpaRepository<CoursePrerequisite, Long> {
    List<CoursePrerequisite> findByCourseId(Long courseId);
    Optional<CoursePrerequisite> findByCourseIdAndPrerequisiteCourseId(Long courseId, Long prerequisiteCourseId);
}
