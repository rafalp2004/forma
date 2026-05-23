package com.example.demo.workout.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

public record WorkoutSetInputDto(
        @NotBlank(message = "ID ćwiczenia jest wymagane")
        String exerciseId,

        @Min(value = 1, message = "Musisz wykonać przynajmniej 1 powtórzenie")
        Integer reps,

        @PositiveOrZero(message = "Ciężar nie może być ujemny")
        Double weight,

        @NotNull(message = "Czas wykonania serii jest wymagany")
        LocalDateTime performedAt
) {}