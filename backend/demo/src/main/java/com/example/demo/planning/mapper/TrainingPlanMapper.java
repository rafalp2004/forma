package com.example.demo.planning.mapper;

import com.example.demo.planning.dto.PlanExerciseResponse;
import com.example.demo.planning.dto.TrainingPlanRequest;
import com.example.demo.planning.dto.TrainingPlanResponse;
import com.example.demo.planning.entity.PlanStatus;
import com.example.demo.planning.entity.TrainingPlan;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TrainingPlanMapper {

    private final PlanExerciseMapper planExerciseMapper;

    public TrainingPlan toEntity(TrainingPlanRequest request, Long userId) {
        return TrainingPlan.builder()
                .userId(userId)
                .name(request.name())
                .description(request.description())
                .status(PlanStatus.DRAFT)
                .startDate(request.startDate())
                .endDate(request.endDate())
                .build();
    }

    public TrainingPlanResponse toResponse(TrainingPlan plan) {
        List<PlanExerciseResponse> exercises = plan.getExercises()
                .stream()
                .map(planExerciseMapper::toResponse)
                .toList();

        return new TrainingPlanResponse(
                plan.getId(),
                plan.getUserId(),
                plan.getName(),
                plan.getDescription(),
                plan.getStatus().name(),
                plan.getStartDate(),
                plan.getEndDate(),
                plan.getCreatedAt(),
                exercises
        );
    }

    public void updateEntity(TrainingPlan plan, TrainingPlanRequest request) {
        plan.setName(request.name());
        plan.setDescription(request.description());
        plan.setStartDate(request.startDate());
        plan.setEndDate(request.endDate());
    }
}
