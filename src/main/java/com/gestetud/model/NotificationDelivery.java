package com.gestetud.model;
import com.gestetud.notification.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name="notification_deliveries") public class NotificationDelivery {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @ManyToOne(optional=false) @JoinColumn(name="student_id") public Student student;
 @Enumerated(EnumType.STRING) @Column(nullable=false) public NotificationEventType eventType;
 @Enumerated(EnumType.STRING) @Column(nullable=false) public NotificationChannel channel;
 @Column(nullable=false) public String recipient;
 @Column(length=200) public String subject;
 @Column(nullable=false,length=4000) public String body;
 @Enumerated(EnumType.STRING) @Column(nullable=false) public DeliveryStatus status;
 @Column(length=1000) public String errorMessage;
 @Column(nullable=false) public LocalDateTime createdAt=LocalDateTime.now();
 public LocalDateTime sentAt;
 protected NotificationDelivery(){}
 public NotificationDelivery(Student student,NotificationEventType eventType,NotificationChannel channel,String recipient,String subject,String body){
   this.student=student;this.eventType=eventType;this.channel=channel;this.recipient=recipient;this.subject=subject;this.body=body;this.status=DeliveryStatus.PENDING;
 }
}
