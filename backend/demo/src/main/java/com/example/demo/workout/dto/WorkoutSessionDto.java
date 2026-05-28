package com.example.demo.workout.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public record WorkoutSessionDto(
        @NotNull(message = "Brak ID użytkownika")
        Long userId,

        @NotNull(message = "Brak czasu rozpoczęcia treningu")
        LocalDateTime startTime,

        @NotNull(message = "Brak czasu zakończenia treningu")
        LocalDateTime endTime,

        @NotEmpty(message = "Trening musi zawierać przynajmniej jedną serię")
        @Valid
        List<WorkoutSetInputDto> sets
) {}