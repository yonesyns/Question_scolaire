package com.gestetud.repo;
import com.gestetud.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface GradeRepository extends JpaRepository<Grade,Long> {
    List<Grade> findByStudentId(Long id);
    Optional<Grade> findByExamIdAndStudentId(Long examId, Long studentId);
}
