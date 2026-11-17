package com.gestetud.repo;
import com.gestetud.model.CourseEvaluationRule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface CourseEvaluationRuleRepository extends JpaRepository<CourseEvaluationRule,Long> {
 List<CourseEvaluationRule> findByCourseId(Long courseId);
 void deleteByCourseId(Long courseId);
}
