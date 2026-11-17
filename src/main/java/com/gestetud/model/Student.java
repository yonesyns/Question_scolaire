package com.gestetud.model;
import jakarta.persistence.*;
@Entity @Table(name="students") public class Student {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Column(nullable=false) public String name;
 @Column(nullable=false, unique=true) public String email;
 @Column(nullable=false) public String program;
 protected Student() {}
 public Student(String name,String email,String program){this.name=name;this.email=email;this.program=program;}
}
