package com.example.demo.shared.dto;

import java.time.LocalDateTime;

public record WorkoutSummaryDto(
        Long id,
        Long userId,
        LocalDateTime completedAt,
        Double totalVolumeKg,
        Integer totalSets
) {
}
