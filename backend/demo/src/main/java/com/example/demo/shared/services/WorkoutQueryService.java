package com.example.demo.shared.services;

import com.example.demo.shared.dto.ExerciseDto;
import com.example.demo.shared.dto.WorkoutSummaryDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WorkoutQueryService {
    List<WorkoutSummaryDto> getHistory(Long userId, LocalDate from, LocalDate to);

    Optional<WorkoutSummaryDto> getById(Long sessionId);

    List<ExerciseDto> getAllExercises();

    ExerciseDto getExerciseById(String exerciseId);

    List<ExerciseDto> searchExercises(String keyword);
}
