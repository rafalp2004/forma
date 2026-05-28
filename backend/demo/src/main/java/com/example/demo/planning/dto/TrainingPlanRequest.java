package com.example.demo.planning.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record TrainingPlanRequest(
        @NotBlank
        @Size(max = 100)
        String name,

        String description,
        LocalDate startDate,
        LocalDate endDate,
        List<@Valid PlanExerciseRequest> exercises
) {
}
