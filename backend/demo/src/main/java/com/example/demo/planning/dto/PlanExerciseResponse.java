package com.example.demo.planning.dto;

import java.math.BigDecimal;

public record PlanExerciseResponse(
        Long id,
        String exerciseId,
        String exerciseName,
        Integer dayOfWeek,
        Integer sets,
        Integer reps,
        BigDecimal targetWeightKg
) {
}
