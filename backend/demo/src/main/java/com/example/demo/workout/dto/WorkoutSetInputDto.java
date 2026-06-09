package com.example.demo.workout.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record WorkoutSetInputDto(
        @NotBlank(message = "ID ćwiczenia jest wymagane")
        String exerciseId,

        @Min(value = 1, message = "Musisz wykonać przynajmniej 1 powtórzenie")
        Integer reps,

        @NotBlank(message = "Podaj ciężar albo wpisz bw dla ćwiczenia z masą ciała")
        String weight,

        @NotNull(message = "Czas wykonania serii jest wymagany")
        LocalDateTime performedAt
) {
        public WorkoutSetInputDto(String exerciseId, Integer reps, Double weight, LocalDateTime performedAt) {
                this(exerciseId, reps, weight == null ? null : weight.toString(), performedAt);
        }
}
