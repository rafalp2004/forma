package com.example.demo.nutrition.controller;

import org.springframework.web.bind.annotation.*;
import com.example.demo.nutrition.model.FoodProduct;
import com.example.demo.nutrition.service.OpenFoodFactsService;

// import com.forma.shared.auth.CurrentUser;
// import com.forma.shared.dto.UserDto;

import java.util.List;

@RestController
@RequestMapping("/api/nutrition")
@CrossOrigin(origins = "*")
public class NutritionController {

    private final OpenFoodFactsService offService;

    public NutritionController(OpenFoodFactsService offService) {
        this.offService = offService;
    }

    // wyszukiwanie produktów
    @GetMapping("/products")
    public List<FoodProduct> searchProducts(
            @RequestParam("query") String query) {

        return offService.searchProducts(query);
    }
}