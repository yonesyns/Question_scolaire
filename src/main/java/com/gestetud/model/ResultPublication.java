package com.gestetud.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name="result_publications",uniqueConstraints=@UniqueConstraint(columnNames={"student_id","course_id"}))
public class ResultPublication {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @ManyToOne(optional=false) @JoinColumn(name="student_id") public Student student;
 @ManyToOne(optional=false) @JoinColumn(name="course_id") public Course course;
 @Column(nullable=false) public Double average;
 @Column(nullable=false) public LocalDateTime publishedAt=LocalDateTime.now();
 protected ResultPublication(){}
 public ResultPublication(Student student,Course course,Double average){this.student=student;this.course=course;this.average=average;}
}
