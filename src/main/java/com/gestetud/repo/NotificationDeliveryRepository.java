package com.gestetud.repo;
import com.gestetud.model.NotificationDelivery;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface NotificationDeliveryRepository extends JpaRepository<NotificationDelivery,Long> {
 List<NotificationDelivery> findByStudentIdOrderByCreatedAtDesc(Long studentId);
}
