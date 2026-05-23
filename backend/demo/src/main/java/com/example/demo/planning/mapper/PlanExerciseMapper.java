package com.example.demo.planning.mapper;

import com.example.demo.planning.dto.PlanExerciseRequest;
import com.example.demo.planning.dto.PlanExerciseResponse;
import com.example.demo.planning.entity.PlanExercise;
import com.example.demo.planning.entity.TrainingPlan;
import org.springframework.stereotype.Component;

@Component
public class PlanExerciseMapper {

    public PlanExercise toEntity(
            PlanExerciseRequest request,
            TrainingPlan trainingPlan
    ) {
        return PlanExercise.builder()
                .trainingPlan(trainingPlan)
                .exerciseId(request.exerciseId())
                .exerciseName(request.exerciseName())
                .dayOfWeek(request.dayOfWeek())
                .sets(request.sets())
                .reps(request.reps())
                .targetWeightKg(request.targetWeightKg())
                .build();
    }

    public PlanExerciseResponse toResponse(PlanExercise exercise) {
        return new PlanExerciseResponse(
                exercise.getId(),
                exercise.getExerciseId(),
                exercise.getExerciseName(),
                exercise.getDayOfWeek(),
                exercise.getSets(),
                exercise.getReps(),
                exercise.getTargetWeightKg()
        );
    }
}
