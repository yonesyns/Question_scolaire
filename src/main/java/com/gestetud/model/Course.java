package com.gestetud.model;
import jakarta.persistence.*;
@Entity @Table(name="courses") public class Course {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Column(nullable=false,unique=true) public String code;
 @Column(nullable=false) public String title;
 @Column(nullable=false) public Integer capacity;
 protected Course() {}
 public Course(String code,String title,Integer capacity){this.code=code;this.title=title;this.capacity=capacity;}
}
