package com.example.demo.shared.dto;

import java.time.LocalDateTime;
import java.util.List;

public record WorkoutSummaryDto(
        Long id,
        Long userId,
        LocalDateTime completedAt,
        Double totalVolumeKg,
        Integer totalSets,
        List<WorkoutSetDto> sets
) {}
