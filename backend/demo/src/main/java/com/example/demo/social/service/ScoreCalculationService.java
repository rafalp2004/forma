package com.example.demo.social.service;

import com.example.demo.shared.dto.WorkoutSummaryDto;
import com.example.demo.social.entity.ChallengeMetric;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ScoreCalculationService {

    public double calculate(List<WorkoutSummaryDto> history, ChallengeMetric metric) {
        return switch (metric) {
            case TOTAL_VOLUME -> calculateTotalVolume(history);
            case WORKOUT_COUNT -> history.size();
            case STREAK_DAYS -> calculateStreakDays(history);
        };
    }

    private double calculateTotalVolume(List<WorkoutSummaryDto> history) {
        return history.stream()
                .flatMap(session -> session.sets().stream())
                .mapToDouble(set -> set.weightKg() * set.reps())
                .sum();
    }

    private double calculateStreakDays(List<WorkoutSummaryDto> history) {
        if (history.isEmpty()) return 0;

        List<LocalDate> sortedDays = history.stream()
                .map(s -> s.completedAt().toLocalDate())
                .distinct()
                .sorted()
                .toList();

        int maxStreak = 1;
        int current = 1;
        for (int i = 1; i < sortedDays.size(); i++) {
            if (sortedDays.get(i).minusDays(1).equals(sortedDays.get(i - 1))) {
                current++;
                maxStreak = Math.max(maxStreak, current);
            } else {
                current = 1;
            }
        }
        return maxStreak;
    }
}
