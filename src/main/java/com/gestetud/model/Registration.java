package com.gestetud.model;
import jakarta.persistence.*;
@Entity @Table(name="registrations", uniqueConstraints=@UniqueConstraint(columnNames={"student_id","course_id"})) public class Registration {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @ManyToOne(optional=false) public Student student;
 @ManyToOne(optional=false) public Course course;
 @Column(nullable=false) public String status="ACTIVE";
 protected Registration() {}
 public Registration(Student s,Course c){student=s;course=c;}
}
