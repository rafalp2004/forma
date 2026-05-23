package com.example.demo.planning.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Max;

import java.math.BigDecimal;

public record PlanExerciseRequest(
        @NotBlank
        String exerciseId,

        @NotBlank
        String exerciseName,

        @NotNull
        @Min(1)
        @Max(7)
        Integer dayOfWeek,

        @NotNull
        @Min(1)
        Integer sets,

        @NotNull
        @Min(1)
        Integer reps,

        BigDecimal targetWeightKg
) {
}
