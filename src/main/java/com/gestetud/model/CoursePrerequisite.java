package com.gestetud.model;

import jakarta.persistence.*;

@Entity
@Table(name = "course_prerequisites", uniqueConstraints =
        @UniqueConstraint(columnNames = {"course_id", "prerequisite_course_id"}))
public class CoursePrerequisite {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    /** Le module auquel on veut s'inscrire. */
    @ManyToOne(optional = false) @JoinColumn(name = "course_id")
    public Course course;

    /** Module dont la validation est demandée. */
    @ManyToOne(optional = false) @JoinColumn(name = "prerequisite_course_id")
    public Course prerequisiteCourse;

    /** Module équivalent accepté à la place du prérequis principal, facultatif. */
    @ManyToOne @JoinColumn(name = "equivalent_course_id")
    public Course equivalentCourse;

    @Column(nullable = false)
    public Double minimumGrade = 10.0;

    protected CoursePrerequisite() {}

    public CoursePrerequisite(Course course, Course prerequisiteCourse, Course equivalentCourse, Double minimumGrade) {
        this.course = course;
        this.prerequisiteCourse = prerequisiteCourse;
        this.equivalentCourse = equivalentCourse;
        this.minimumGrade = minimumGrade == null ? 10.0 : minimumGrade;
    }
}
