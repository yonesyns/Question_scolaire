package com.gestetud.model;
import com.gestetud.notification.*;
import jakarta.persistence.*;
@Entity @Table(name="notification_templates",uniqueConstraints=@UniqueConstraint(columnNames={"event_type","channel"}))
public class NotificationTemplate {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Enumerated(EnumType.STRING) @Column(name="event_type",nullable=false) public NotificationEventType eventType;
 @Enumerated(EnumType.STRING) @Column(nullable=false) public NotificationChannel channel;
 @Column(length=200) public String subject;
 @Column(nullable=false,length=4000) public String body;
 protected NotificationTemplate(){}
 public NotificationTemplate(NotificationEventType eventType,NotificationChannel channel,String subject,String body){this.eventType=eventType;this.channel=channel;this.subject=subject;this.body=body;}
}
