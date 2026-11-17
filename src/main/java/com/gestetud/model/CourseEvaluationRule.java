package com.gestetud.model;
import jakarta.persistence.*;
@Entity @Table(name="course_evaluation_rules",uniqueConstraints=@UniqueConstraint(columnNames={"course_id","evaluation_type_id"}))
public class CourseEvaluationRule {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @ManyToOne(optional=false) @JoinColumn(name="course_id") public Course course;
 @ManyToOne(optional=false) @JoinColumn(name="evaluation_type_id") public EvaluationType evaluationType;
 @Column(nullable=false) public Double weight;
 protected CourseEvaluationRule() {}
 public CourseEvaluationRule(Course course,EvaluationType type,Double weight){this.course=course;this.evaluationType=type;this.weight=weight;}
}
