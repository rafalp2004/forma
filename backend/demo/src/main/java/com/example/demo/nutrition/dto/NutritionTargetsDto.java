package com.example.demo.nutrition.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NutritionTargetsDto {
    private Integer kcal;
    private Integer protein;
    private Integer fat;
    private Integer carbs;
}