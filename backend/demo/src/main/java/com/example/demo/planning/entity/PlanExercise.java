package com.example.demo.planning.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "plan_exercises")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class PlanExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private TrainingPlan trainingPlan;

    // ID ćwiczenia z modułu ExerciseDB / workout
    @Column(nullable = false, length = 100)
    private String exerciseId;

    @Column(nullable = false, length = 150)
    private String exerciseName;

    // 1 = poniedziałek, 7 = niedziela
    @Column(nullable = false)
    private Integer dayOfWeek;

    @Column(nullable = false)
    private Integer sets;

    @Column(nullable = false)
    private Integer reps;

    @Column(precision = 6, scale = 2)
    private BigDecimal targetWeightKg;
}
