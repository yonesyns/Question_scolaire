package com.gestetud.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="registrations", uniqueConstraints=@UniqueConstraint(columnNames={"student_id","course_id"}))
public class Registration {
    public static final String ACTIVE = "ACTIVE";
    public static final String WAITING_LIST = "WAITING_LIST";
    public static final String CANCELLED = "CANCELLED";

    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    public Long id;
    @ManyToOne(optional=false) public Student student;
    @ManyToOne(optional=false) public Course course;
    @Column(nullable=false) public String status = ACTIVE;
    @Column(name="created_at") public LocalDateTime createdAt;

    protected Registration() {}
    public Registration(Student student, Course course) { this.student=student;this.course=course; }
    @PrePersist
    protected void setCreationTime() { if(createdAt==null) createdAt=LocalDateTime.now(); }
}
