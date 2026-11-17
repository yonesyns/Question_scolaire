package com.gestetud.repo;
import com.gestetud.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import java.time.LocalDate;
import java.util.Optional;
public interface ExamRepository extends JpaRepository<Exam,Long> {
    @EntityGraph(attributePaths={"course","evaluationType"})
    Optional<Exam> findWithDetailsById(Long id);
    Optional<Exam> findByCourseIdAndDateAndTitle(Long courseId, LocalDate date, String title);
}
