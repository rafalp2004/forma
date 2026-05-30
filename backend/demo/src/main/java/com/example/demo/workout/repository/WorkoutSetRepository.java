package com.example.demo.workout.repository;

import com.example.demo.workout.entity.WorkoutSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WorkoutSetRepository extends JpaRepository<WorkoutSet, Long> {

    @Query("SELECT ws FROM WorkoutSet ws WHERE ws.session.id = :sessionId")
    List<WorkoutSet> findBySessionId(@Param("sessionId") Long sessionId);
}