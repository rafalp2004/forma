package com.example.demo.nutrition.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class MealEntryDto {
    private Long id;
    private LocalDate consumptionDate;
    private String mealType;
    private String productName;
    private String externalProductId;
    private float weightInGrams;
    private float calculatedCalories;
    private float calculatedProtein;
    private float calculatedCarbohydrates;
    private float calculatedFat;
}