package com.example.demo.nutrition.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "daily_summaries")
@Data
public class DailySummary {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Long userId;

    private LocalDate date;

    private float totalCalories;
    private float totalProtein;
    private float totalCarbohydrates;
    private float totalFat;

    private float dailyCalorieLimit;
}