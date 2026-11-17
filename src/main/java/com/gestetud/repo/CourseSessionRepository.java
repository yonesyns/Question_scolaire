package com.gestetud.repo;
import com.gestetud.model.CourseSession;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface CourseSessionRepository extends JpaRepository<CourseSession,Long> { List<CourseSession> findByCourseIdOrderByDate(Long courseId); }
