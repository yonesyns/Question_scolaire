package com.gestetud.api.dto;
public record ExamEligibility(Long studentId,Long courseId,int sessionsHeld,int absences,double absencePercentage,boolean eligible,String reason){}
