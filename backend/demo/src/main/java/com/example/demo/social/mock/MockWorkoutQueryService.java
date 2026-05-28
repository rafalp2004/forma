package com.example.demo.social.mock;

import com.example.demo.shared.dto.ExerciseDto;
import com.example.demo.shared.dto.WorkoutSetDto;
import com.example.demo.shared.dto.WorkoutSummaryDto;
import com.example.demo.shared.services.WorkoutQueryService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

public class MockWorkoutQueryService implements WorkoutQueryService {

    @Override
    public List<WorkoutSummaryDto> getHistory(Long userId, LocalDate from, LocalDate to) {
        LocalDate day1 = from;
        LocalDate day2 = from.plusDays(1);
        LocalDate day3 = from.plusDays(3);

        if (day3.isAfter(to)) return List.of();

        return List.of(
                new WorkoutSummaryDto(1L, userId, day1.atTime(10, 0), 2400.0, 6,
                        List.of(
                                new WorkoutSetDto("ex_bench", "Bench Press", "chest", 10, 80.0, day1.atTime(10, 0)),
                                new WorkoutSetDto("ex_bench", "Bench Press", "chest", 8, 85.0, day1.atTime(10, 5)),
                                new WorkoutSetDto("ex_squat", "Squat", "legs", 10, 100.0, day1.atTime(10, 15))
                        )),
                new WorkoutSummaryDto(2L, userId, day2.atTime(11, 0), 1800.0, 4,
                        List.of(
                                new WorkoutSetDto("ex_dl", "Deadlift", "back", 6, 120.0, day2.atTime(11, 0)),
                                new WorkoutSetDto("ex_dl", "Deadlift", "back", 5, 125.0, day2.atTime(11, 10))
                        )),
                new WorkoutSummaryDto(3L, userId, day3.atTime(9, 0), 2000.0, 5,
                        List.of(
                                new WorkoutSetDto("ex_ohp", "OHP", "shoulders", 10, 60.0, day3.atTime(9, 0)),
                                new WorkoutSetDto("ex_row", "Barbell Row", "back", 10, 70.0, day3.atTime(9, 10))
                        ))
        );
    }

    @Override
    public List<ExerciseDto> getAllExercises() {
        return List.of();
    }

    @Override
    public ExerciseDto getExerciseById(String exerciseId) {
        return null;
    }

    @Override
    public List<ExerciseDto> searchExercises(String keyword) {
        return List.of();
    }
}
