package com.gestetud.model;
import jakarta.persistence.*;
@Entity @Table(name="attendances",uniqueConstraints=@UniqueConstraint(columnNames={"session_id","student_id"})) public class Attendance {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @ManyToOne(optional=false) @JoinColumn(name="session_id") public CourseSession session;
 @ManyToOne(optional=false) @JoinColumn(name="student_id") public Student student;
 @Enumerated(EnumType.STRING) @Column(nullable=false) public AttendanceStatus status;
 protected Attendance(){}
 public Attendance(CourseSession session,Student student,AttendanceStatus status){this.session=session;this.student=student;this.status=status;}
}
