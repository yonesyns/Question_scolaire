package com.gestetud.api.dto;
import java.util.Map;
public record CourseResult(Long studentId, Long courseId, String courseCode, String courseTitle,
                           Double finalAverage, Double provisionalAverage, Double coveredWeight,
                           boolean complete, boolean eligibleForFinalExam, double absencePercentage, String publicationStatus, Map<String, Double> averagesByType) {}
