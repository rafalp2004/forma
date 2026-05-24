package com.example.demo.nutrition.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "meal_entries")
@Data
public class MealEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private FoodProduct product;

    private LocalDate consumptionDate;
    private String mealType;
    private float weightInGrams;

    private float calculatedCalories;
    private float calculatedProtein;
    private float calculatedCarbohydrates;
    private float calculatedFat;
}