package com.gestetud.model;
import jakarta.persistence.*; import java.time.LocalDate;
import com.gestetud.external.*;
@Entity @Table(name="exams") public class Exam {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @ManyToOne(optional=false) public Course course;
 @Column(nullable=false) public LocalDate date;
 @Column(nullable=false) public String title;
 @ManyToOne @JoinColumn(name="evaluation_type_id") public EvaluationType evaluationType;
 @Column public Boolean finalExam=false;
 @Column(name="external_exam_id") public String externalExamId;
 @Enumerated(EnumType.STRING) @Column(name="external_status") public ExternalExamStatus externalStatus;
 @Column(name="external_raw_status") public String externalRawStatus;
 @Enumerated(EnumType.STRING) @Column(name="external_sync_status") public ExternalSyncStatus externalSyncStatus=ExternalSyncStatus.NOT_SYNCED;
 @Column(name="external_error",length=1000) public String externalError;
 protected Exam() {}
 public Exam(Course c,LocalDate d,String t){course=c;date=d;title=t;}
 public Exam(Course c,LocalDate d,String t,EvaluationType type){course=c;date=d;title=t;evaluationType=type;}
 public Exam(Course c,LocalDate d,String t,EvaluationType type,Boolean finalExam){course=c;date=d;title=t;evaluationType=type;this.finalExam=finalExam;}
}
