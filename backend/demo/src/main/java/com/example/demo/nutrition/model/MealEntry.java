package com.example.demo.nutrition.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "diet_meals")
@Data
public class MealEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String productName;
    private String externalProductId;

    private LocalDate consumptionDate;

    private String mealType;

    private float weightInGrams;
    private float calculatedCalories;
    private float calculatedProtein;
    private float calculatedCarbohydrates;
    private float calculatedFat;
}