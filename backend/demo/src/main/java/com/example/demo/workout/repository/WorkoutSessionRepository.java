package com.example.demo.workout.repository;

import com.example.demo.workout.entity.WorkoutSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, Long> {

    @Query("SELECT ws FROM WorkoutSession ws " +
            "WHERE ws.userId = :userId " +
            "AND ws.endTime IS NOT NULL " +
            "AND ws.endTime >= :startOfDay " +
            "AND ws.endTime <= :endOfDay " +
            "ORDER BY ws.endTime ASC")
    List<WorkoutSession> findCompletedHistory(
            @Param("userId") Long userId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );

    long countByUserId(Long userId);
}