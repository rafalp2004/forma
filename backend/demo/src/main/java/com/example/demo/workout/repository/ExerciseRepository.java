package com.example.demo.workout.repository;

import com.example.demo.workout.entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExerciseRepository extends JpaRepository<Exercise, String> {
    List<Exercise> findByMuscleGroupContainingIgnoreCaseOrNameContainingIgnoreCase(String muscleGroup, String name);
}