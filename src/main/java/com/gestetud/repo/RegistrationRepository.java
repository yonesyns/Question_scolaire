package com.gestetud.repo;

import com.gestetud.model.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface RegistrationRepository extends JpaRepository<Registration,Long> {
    long countByCourseIdAndStatus(Long courseId,String status);
    List<Registration> findByStudentId(Long studentId);
    List<Registration> findByCourseIdAndStatusOrderByCreatedAtAscIdAsc(Long courseId,String status);
    List<Registration> findByCourseIdAndStatusOrderByIdAsc(Long courseId,String status);
    Optional<Registration> findByStudentIdAndCourseId(Long studentId,Long courseId);
}
