package com.example.demo.workout.service;

import com.example.demo.shared.dto.ExerciseDto;
import com.example.demo.shared.dto.WorkoutSetDto;
import com.example.demo.shared.dto.WorkoutSummaryDto;
import com.example.demo.shared.services.WorkoutQueryService;
import com.example.demo.workout.entity.Exercise;
import com.example.demo.workout.entity.WorkoutSession;
import com.example.demo.workout.entity.WorkoutSet;
import com.example.demo.workout.repository.ExerciseRepository;
import com.example.demo.workout.repository.WorkoutSessionRepository;
import com.example.demo.workout.repository.WorkoutSetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkoutQueryServiceImpl implements WorkoutQueryService {

    private final ExerciseRepository exerciseRepository;
    private final WorkoutSessionRepository workoutSessionRepository;
    private final WorkoutSetRepository workoutSetRepository;

    @Override
    public List<WorkoutSummaryDto> getHistory(Long userId, LocalDate from, LocalDate to) {
        if (userId == null || from == null || to == null) {
            throw new IllegalArgumentException("Parametry userId, from oraz to nie mogą być puste.");
        }
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("Data 'from' nie może być późniejsza niż data 'to'.");
        }

        LocalDateTime startOfDay = from.atStartOfDay();
        LocalDateTime endOfDay = to.atTime(LocalTime.MAX);

        List<WorkoutSession> sessions = workoutSessionRepository.findCompletedHistory(userId, startOfDay, endOfDay);

        if (sessions.isEmpty()) {
            return new ArrayList<>();
        }

        Set<String> neededExerciseIds = sessions.stream()
                .filter(s -> s.getSets() != null)
                .flatMap(s -> s.getSets().stream())
                .map(WorkoutSet::getExerciseId)
                .collect(Collectors.toSet());

        Map<String, Exercise> exerciseDictionary = exerciseRepository.findAllById(neededExerciseIds).stream()
                .collect(Collectors.toMap(Exercise::getId, ex -> ex));

        return sessions.stream().map(session -> {
            List<WorkoutSetDto> setDtos = new ArrayList<>();
            double totalVolume = session.getTotalVolume() != null ? session.getTotalVolume() : 0.0;
            int totalSets = 0;

            if (session.getSets() != null && !session.getSets().isEmpty()) {
                for (WorkoutSet set : session.getSets()) {
                    Exercise exercise = exerciseDictionary.get(set.getExerciseId());
                    String exerciseName = exercise != null ? exercise.getName() : "Nieznane ćwiczenie";
                    String muscleGroup = exercise != null ? exercise.getMuscleGroup() : "Nieznana partia";

                    double weight = set.getWeight() != null ? set.getWeight() : 0.0;
                    int reps = set.getReps() != null ? set.getReps() : 0;

                    setDtos.add(new WorkoutSetDto(
                            set.getExerciseId(),
                            exerciseName,
                            muscleGroup,
                            reps,
                            weight,
                            set.getPerformedAt()
                    ));
                }
                totalSets = session.getSets().size();
            }

            return new WorkoutSummaryDto(
                    session.getId(),
                    session.getUserId(),
                    session.getEndTime(),
                    totalVolume,
                    totalSets,
                    setDtos
            );
        }).toList();
    }

    @Override
    public Optional<WorkoutSummaryDto> getById(Long sessionId) {
        return workoutSessionRepository.findById(sessionId).map(session -> {
            List<WorkoutSet> sets = workoutSetRepository.findBySessionId(sessionId);

            Set<String> neededExerciseIds = sets.stream()
                    .map(WorkoutSet::getExerciseId)
                    .collect(Collectors.toSet());

            Map<String, Exercise> exerciseDictionary = exerciseRepository.findAllById(neededExerciseIds).stream()
                    .collect(Collectors.toMap(Exercise::getId, ex -> ex));

            List<WorkoutSetDto> setDtos = sets.stream().map(set -> {
                Exercise exercise = exerciseDictionary.get(set.getExerciseId());
                return new WorkoutSetDto(
                        set.getExerciseId(),
                        exercise != null ? exercise.getName() : "Nieznane cwiczenie",
                        exercise != null ? exercise.getMuscleGroup() : "Nieznana partia",
                        set.getReps() != null ? set.getReps() : 0,
                        set.getWeight() != null ? set.getWeight() : 0.0,
                        set.getPerformedAt()
                );
            }).toList();

            return new WorkoutSummaryDto(
                    session.getId(),
                    session.getUserId(),
                    session.getEndTime(),
                    session.getTotalVolume() != null ? session.getTotalVolume() : 0.0,
                    setDtos.size(),
                    setDtos
            );
        });
    }

    @Override
    public List<ExerciseDto> getAllExercises() {
        return exerciseRepository.findAll().stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public ExerciseDto getExerciseById(String id) {
        return exerciseRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono ćwiczenia o ID: " + id));
    }

    @Override
    public List<ExerciseDto> searchExercises(String keyword) {
        return exerciseRepository
                .findByMuscleGroupContainingIgnoreCaseOrNameContainingIgnoreCase(keyword, keyword)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    private ExerciseDto mapToDto(Exercise exercise) {
        return new ExerciseDto(
                exercise.getId(),
                exercise.getName(),
                exercise.getMuscleGroup(),
                exercise.getEquipment(),
                exercise.getGifUrl(),
                exercise.getInstructions()
        );
    }
}