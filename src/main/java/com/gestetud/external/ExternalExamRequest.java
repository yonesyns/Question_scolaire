package com.gestetud.external;
import java.time.LocalDate;
public record ExternalExamRequest(Long sourceExamId,String courseCode,String title,LocalDate date,String evaluationType,Boolean finalExam){}
