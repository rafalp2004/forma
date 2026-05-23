package com.example.demo.planning.repository;

import com.example.demo.planning.entity.PlanExercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanExerciseRepository extends JpaRepository<PlanExercise, Long> {

    List<PlanExercise> findByTrainingPlanId(Long planId);

    void deleteByTrainingPlanId(Long planId);
}
