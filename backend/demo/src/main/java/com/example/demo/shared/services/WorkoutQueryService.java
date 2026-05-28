package com.example.demo.shared.services;

import com.example.demo.shared.dto.ExerciseDto;
import com.example.demo.shared.dto.WorkoutSummaryDto;

import java.time.LocalDate;
import java.util.List;

public interface WorkoutQueryService {
    List<WorkoutSummaryDto> getHistory(Long userId, LocalDate from, LocalDate to);

    List<ExerciseDto> getAllExercises();

    ExerciseDto getExerciseById(String exerciseId);

    List<ExerciseDto> searchExercises(String keyword);
}
