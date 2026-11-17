package com.gestetud.repo;
import com.gestetud.model.NotificationTemplate;
import com.gestetud.notification.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate,Long> {
 Optional<NotificationTemplate> findByEventTypeAndChannel(NotificationEventType eventType,NotificationChannel channel);
}
