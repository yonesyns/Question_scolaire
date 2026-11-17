package com.gestetud.repo;
import com.gestetud.model.EvaluationType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface EvaluationTypeRepository extends JpaRepository<EvaluationType,Long> { Optional<EvaluationType> findByCode(String code); }
