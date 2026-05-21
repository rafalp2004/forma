package com.example.demo.shared.dto;

import java.time.LocalDateTime;

public record WorkoutSetDto(
        String exerciseId,
        String exerciseName,
        String muscleGroup,
        Integer reps,
        Double weightKg,
        LocalDateTime performedAt
) {}
