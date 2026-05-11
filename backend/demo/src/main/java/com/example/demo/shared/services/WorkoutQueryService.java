package com.example.demo.shared.services;

import com.example.demo.shared.dto.WorkoutSummaryDto;

import java.time.LocalDate;
import java.util.List;

public interface WorkoutQueryService {
    List<WorkoutSummaryDto> getHistory(Long userId, LocalDate from, LocalDate to);
}
