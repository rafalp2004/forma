package com.example.demo.workout.repository;

import com.example.demo.workout.entity.WorkoutSet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutSetRepository extends JpaRepository<WorkoutSet, Long> {
}