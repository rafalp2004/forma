package com.example.demo.workout.service;

import com.example.demo.workout.dto.WorkoutSessionDto;
import com.example.demo.workout.entity.WorkoutSession;
import com.example.demo.workout.entity.WorkoutSet;
import com.example.demo.workout.repository.WorkoutSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkoutCommandService {

    private final WorkoutSessionRepository workoutSessionRepository;

    @Transactional
    public void saveWorkoutSession(WorkoutSessionDto dto) {
        WorkoutSession session = new WorkoutSession();
        session.setUserId(dto.userId());
        session.setStartTime(dto.startTime());
        session.setEndTime(dto.endTime());

        double calculatedVolume = 0.0;

        if (dto.sets() != null) {
            List<WorkoutSet> sets = dto.sets().stream().map(setDto -> {
                WorkoutSet set = new WorkoutSet();
                set.setExerciseId(setDto.exerciseId());
                set.setReps(setDto.reps());
                set.setWeight(setDto.weight());
                set.setPerformedAt(setDto.performedAt());

                session.addSet(set);
                return set;
            }).toList();

            calculatedVolume = sets.stream()
                    .mapToDouble(s -> (s.getWeight() != null ? s.getWeight() : 0.0) * (s.getReps() != null ? s.getReps() : 0))
                    .sum();
        }

        session.setTotalVolume(calculatedVolume);
        workoutSessionRepository.save(session);
    }
}