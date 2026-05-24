package com.example.demo.planning.service;

import com.example.demo.planning.dto.CalendarWorkoutResponse;
import com.example.demo.planning.dto.StrengthProgressPointResponse;
import com.example.demo.planning.dto.TrainingPlanRequest;
import com.example.demo.planning.dto.TrainingPlanResponse;
import com.example.demo.planning.dto.WeightEntryRequest;
import com.example.demo.planning.dto.WeightProgressPointResponse;

import java.util.List;

public interface PlanningService {
    List<TrainingPlanResponse> getPlans(Long userId);

    TrainingPlanResponse createPlan(Long userId, TrainingPlanRequest request);

    TrainingPlanResponse getPlan(Long id);

    TrainingPlanResponse updatePlan(Long id, TrainingPlanRequest request);

    void deletePlan(Long id);

    List<StrengthProgressPointResponse> getStrengthProgress(Long userId, String exerciseId);

    List<WeightProgressPointResponse> getWeightProgress(Long userId);

    WeightProgressPointResponse saveWeightEntry(WeightEntryRequest request);

    List<CalendarWorkoutResponse> getCalendar(Long userId, Integer month, Integer year);
}
