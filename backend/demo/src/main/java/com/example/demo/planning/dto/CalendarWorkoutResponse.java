package com.example.demo.planning.dto;

import java.time.LocalDate;

public record CalendarWorkoutResponse(
        LocalDate date,
        Long workoutId,
        String workoutName
) {
}
