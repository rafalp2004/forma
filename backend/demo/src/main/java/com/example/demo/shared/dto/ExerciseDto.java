package com.example.demo.shared.dto;

public record ExerciseDto(
        String id,
        String name,
        String muscleGroup,
        String equipment,
        String gifUrl,
        String instructions
) {}