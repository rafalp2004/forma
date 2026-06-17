package com.example.demo.nutrition.controller;

import com.example.demo.auth.entity.User;
import com.example.demo.auth.repository.UserRepository;
import com.example.demo.nutrition.dto.NutritionTargetsDto;
import com.example.demo.nutrition.model.FoodProduct;
import com.example.demo.nutrition.model.NutritionTarget;
import com.example.demo.nutrition.repository.NutritionTargetRepository;
import com.example.demo.nutrition.service.NutritionService;
import com.example.demo.nutrition.service.OpenFoodFactsService;
import com.example.demo.nutrition.repository.MealEntryRepository;
import com.example.demo.nutrition.model.MealEntry;
import com.example.demo.nutrition.dto.MealEntryDto;
import java.time.LocalDate;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/nutrition")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class NutritionController {

    private final OpenFoodFactsService offService;
    private final NutritionService nutritionService;
    private final UserRepository userRepository;
    private final MealEntryRepository mealEntryRepository;
    private final NutritionTargetRepository nutritionTargetRepository;

    @GetMapping("/meals")
    public ResponseEntity<List<MealEntryDto>> getMealsByDate(
            Authentication authentication,
            @RequestParam("date") String dateString) {

        String username = authentication.getPrincipal() instanceof User
                ? ((User) authentication.getPrincipal()).getUsername()
                : authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika"));

        LocalDate date = LocalDate.parse(dateString);

        List<MealEntry> meals = mealEntryRepository.findByUserIdAndConsumptionDate(user.getId(), date);

        List<MealEntryDto> dtos = meals.stream().map(meal -> {
            MealEntryDto dto = new MealEntryDto();
            dto.setId(meal.getId());
            dto.setConsumptionDate(meal.getConsumptionDate());
            dto.setMealType(meal.getMealType());
            dto.setProductName(meal.getProductName());
            dto.setExternalProductId(meal.getExternalProductId());
            dto.setWeightInGrams(meal.getWeightInGrams());
            dto.setCalculatedCalories(meal.getCalculatedCalories());
            dto.setCalculatedProtein(meal.getCalculatedProtein());
            dto.setCalculatedCarbohydrates(meal.getCalculatedCarbohydrates());
            dto.setCalculatedFat(meal.getCalculatedFat());
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/meals")
    public ResponseEntity<MealEntryDto> addMeal(
            Authentication authentication,
            @RequestBody MealEntryDto requestDto) {

        String username = authentication.getPrincipal() instanceof User
                ? ((User) authentication.getPrincipal()).getUsername()
                : authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika"));

        MealEntry newMeal = new MealEntry();
        newMeal.setUserId(user.getId());
        newMeal.setConsumptionDate(requestDto.getConsumptionDate());

        newMeal.setMealType(requestDto.getMealType() != null ? requestDto.getMealType() : "SNACK");

        newMeal.setProductName(requestDto.getProductName());
        newMeal.setExternalProductId(requestDto.getExternalProductId());
        newMeal.setWeightInGrams(requestDto.getWeightInGrams());
        newMeal.setCalculatedCalories(requestDto.getCalculatedCalories());
        newMeal.setCalculatedProtein(requestDto.getCalculatedProtein());
        newMeal.setCalculatedCarbohydrates(requestDto.getCalculatedCarbohydrates());
        newMeal.setCalculatedFat(requestDto.getCalculatedFat());

        MealEntry savedMeal = mealEntryRepository.save(newMeal);
        requestDto.setId(savedMeal.getId());

        return ResponseEntity.ok(requestDto);
    }

    @GetMapping("/products")
    public List<FoodProduct> searchProducts(
            @RequestParam("query") String query) {

        return offService.searchProducts(query);
    }

    @GetMapping("/targets")
    public ResponseEntity<NutritionTargetsDto> getMyTargets(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401).build();
        }

        String username;

        if (authentication.getPrincipal() instanceof User) {
            username = ((User) authentication.getPrincipal()).getUsername();
        } else {
            username = authentication.getName();
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono w bazie profilu: " + username));

        NutritionTargetsDto targets = nutritionService.calculateDailyNeeds(user);

        return ResponseEntity.ok(targets);
    }

    @PutMapping("/targets")
    public ResponseEntity<NutritionTargetsDto> setManualTargets(
            Authentication authentication,
            @RequestBody NutritionTargetsDto newTargets) {

        String username = authentication.getPrincipal() instanceof User
                ? ((User) authentication.getPrincipal()).getUsername()
                : authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika"));

        NutritionTarget target = nutritionTargetRepository.findByUserId(user.getId())
                .orElse(new NutritionTarget());

        target.setUserId(user.getId());
        target.setTargetKcal(newTargets.getKcal());
        target.setTargetProtein(newTargets.getProtein());
        target.setTargetFat(newTargets.getFat());
        target.setTargetCarbs(newTargets.getCarbs());
        target.setUseManualTargets(true);

        nutritionTargetRepository.save(target); // Zapisujemy w TWOJEJ encji

        return ResponseEntity.ok(newTargets);
    }

    @PutMapping("/targets/reset")
    public ResponseEntity<NutritionTargetsDto> resetTargetsToAuto(Authentication authentication) {
        String username = authentication.getPrincipal() instanceof User
                ? ((User) authentication.getPrincipal()).getUsername()
                : authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika"));

        Optional<NutritionTarget> targetOpt = nutritionTargetRepository.findByUserId(user.getId());
        if (targetOpt.isPresent()) {
            NutritionTarget target = targetOpt.get();
            target.setUseManualTargets(false);
            nutritionTargetRepository.save(target);
        }

        NutritionTargetsDto recalculatedTargets = nutritionService.calculateDailyNeeds(user);
        return ResponseEntity.ok(recalculatedTargets);
    }

    @DeleteMapping("/meals/{id}")
    public ResponseEntity<Void> deleteMeal(
            Authentication authentication,
            @PathVariable Long id) {

        String username = authentication.getPrincipal() instanceof User
                ? ((User) authentication.getPrincipal()).getUsername()
                : authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika"));

        MealEntry meal = mealEntryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono posiłku"));

        if (!meal.getUserId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        mealEntryRepository.delete(meal);
        return ResponseEntity.ok().build();
    }
}