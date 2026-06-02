package com.example.demo.planning.dto;

import com.example.demo.shared.dto.WorkoutSetDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record CalendarWorkoutResponse(
        LocalDate date,
        Long workoutId,
        String workoutName,
        LocalDateTime completedAt,
        Double totalVolumeKg,
        Integer totalSets,
        List<WorkoutSetDto> sets
) {
}
