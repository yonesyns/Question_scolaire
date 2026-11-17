package com.gestetud.repo;
import com.gestetud.model.ResultPublication;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface ResultPublicationRepository extends JpaRepository<ResultPublication,Long> {
 Optional<ResultPublication> findByStudentIdAndCourseId(Long studentId,Long courseId);
}
