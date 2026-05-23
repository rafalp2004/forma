package com.example.demo.planning.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record TrainingPlanResponse(
        Long id,
        Long userId,
        String name,
        String description,
        String status,
        LocalDate startDate,
        LocalDate endDate,
        LocalDateTime createdAt,
        List<PlanExerciseResponse> exercises
) {
}
