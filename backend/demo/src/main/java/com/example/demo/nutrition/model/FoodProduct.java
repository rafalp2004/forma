package com.example.demo.nutrition.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "food_products")
@Data //
public class FoodProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String externalId;

    private String name;
    private float kcalPer100g;
    private float proteinPer100g;
    private float carbsPer100g;
    private float fatPer100g;
}
