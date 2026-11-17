package com.gestetud.model;
import jakarta.persistence.*;
@Entity @Table(name="evaluation_types") public class EvaluationType {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Column(nullable=false,unique=true) public String code;
 @Column(nullable=false) public String label;
 protected EvaluationType() {}
 public EvaluationType(String code,String label){this.code=code;this.label=label;}
}
