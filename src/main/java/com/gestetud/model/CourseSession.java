package com.gestetud.model;
import jakarta.persistence.*;
import java.time.LocalDate;
@Entity @Table(name="course_sessions") public class CourseSession {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @ManyToOne(optional=false) @JoinColumn(name="course_id") public Course course;
 @Column(nullable=false) public LocalDate date;
 @Column(nullable=false) public String title;
 protected CourseSession(){}
 public CourseSession(Course course,LocalDate date,String title){this.course=course;this.date=date;this.title=title;}
}
