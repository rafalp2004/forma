package com.example.demo.planning.repository;

import com.example.demo.planning.entity.PlanStatus;
import com.example.demo.planning.entity.TrainingPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrainingPlanRepository extends JpaRepository<TrainingPlan, Long> {
    List<TrainingPlan> findByUserId(Long userId);

    List<TrainingPlan> findByUserIdAndStatus(
            Long userId,
            PlanStatus status
    );

    Optional<TrainingPlan> findByIdAndUserId(
            Long id,
            Long userId
    );
}
