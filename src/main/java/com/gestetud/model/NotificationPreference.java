package com.gestetud.model;
import jakarta.persistence.*;
@Entity @Table(name="notification_preferences") public class NotificationPreference {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @OneToOne(optional=false) @JoinColumn(name="student_id",unique=true) public Student student;
 @Column(nullable=false) public boolean emailEnabled=true;
 @Column(nullable=false) public boolean smsEnabled=false;
 @Column(nullable=false) public boolean whatsappEnabled=false;
 @Column(length=32) public String phoneNumber;
 protected NotificationPreference(){}
 public NotificationPreference(Student student){this.student=student;}
}
