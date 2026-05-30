package com.example.demo.workout.repository;

import com.example.demo.workout.entity.WorkoutSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WorkoutSetRepository extends JpaRepository<WorkoutSet, Long> {

    @Query(value = "SELECT ws.id, ws.session_id, ws.exercise_id, ws.reps, ws.weight, ws.performed_at " +
            "FROM workout_sets ws " +
            "JOIN workout_sessions s ON ws.session_id = s.id " +
            "WHERE s.user_id = :userId " +
            "AND ws.weight = (" +
            "  SELECT MAX(ws2.weight) " +
            "  FROM workout_sets ws2 " +
            "  JOIN workout_sessions s2 ON ws2.session_id = s2.id " +
            "  WHERE s2.user_id = :userId AND ws2.exercise_id = ws.exercise_id" +
            ") " +
            "AND ws.performed_at = (" +
            "  SELECT MAX(ws3.performed_at) " +
            "  FROM workout_sets ws3 " +
            "  JOIN workout_sessions s3 ON ws3.session_id = s3.id " +
            "  WHERE s3.user_id = :userId AND ws3.exercise_id = ws.exercise_id AND ws3.weight = ws.weight" +
            ")", nativeQuery = true)
    List<WorkoutSet> findPersonalRecordsByUserId(@Param("userId") Long userId);
}