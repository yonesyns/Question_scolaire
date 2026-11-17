package com.gestetud.repo;
import com.gestetud.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;
public interface ExamRepository extends JpaRepository<Exam,Long> {
    Optional<Exam> findByCourseIdAndDateAndTitle(Long courseId, LocalDate date, String title);
}
