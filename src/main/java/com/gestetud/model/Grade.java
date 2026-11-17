package com.gestetud.model;
import jakarta.persistence.*;
@Entity @Table(name="grades", uniqueConstraints=@UniqueConstraint(columnNames={"exam_id","student_id"})) public class Grade {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @ManyToOne(optional=false) public Exam exam;
 @ManyToOne(optional=false) public Student student;
 @Column(nullable=false) public Double value;
 protected Grade() {}
 public Grade(Exam e,Student s,Double v){exam=e;student=s;value=v;}
}
