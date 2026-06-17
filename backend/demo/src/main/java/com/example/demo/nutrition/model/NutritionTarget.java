package com.example.demo.nutrition.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "nutrition_targets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NutritionTarget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", unique = true, nullable = false)
    private Long userId;

    private Integer targetKcal;
    private Integer targetProtein;
    private Integer targetFat;
    private Integer targetCarbs;
    private Boolean useManualTargets;
}