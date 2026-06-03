package com.example.demo.nutrition.service;

import com.example.demo.auth.entity.User;
import com.example.demo.nutrition.dto.NutritionTargetsDto;
import org.springframework.stereotype.Service;

@Service
public class NutritionService {

    public NutritionTargetsDto calculateDailyNeeds(User user) {
        if (user.getUseManualTargets() != null && user.getUseManualTargets()) {
            return new NutritionTargetsDto(
                    user.getTargetKcal(),
                    user.getTargetProtein(),
                    user.getTargetFat(),
                    user.getTargetCarbs()
            );
        }

        double weight = user.getWeight() != null ? user.getWeight() : 70.0;
        double height = user.getHeight() != null ? user.getHeight() : 175.0;
        int age = user.getAge() != null ? user.getAge() : 25;

        // Wzór Mifflina-St Jeora na BMR
        double bmr = (10 * weight) + (6.25 * height) - (5 * age);

        if (user.getGender() != null && "MALE".equalsIgnoreCase(user.getGender().name())) {
            bmr += 5;
        } else {
            bmr -= 161;
        }

        double activityMultiplier = 1.2;
        if (user.getSessionsPerWeek() != null) {
            int sessions = user.getSessionsPerWeek();
            if (sessions >= 5) activityMultiplier = 1.725;
            else if (sessions >= 3) activityMultiplier = 1.55;
            else if (sessions >= 1) activityMultiplier = 1.375;
        }

        int tdee = (int) Math.round(bmr * activityMultiplier);

        if (user.getGoal() != null) {
            if ("LOSE_WEIGHT".equalsIgnoreCase(user.getGoal().name())) {
                tdee -= 300;
            } else if ("GAIN_WEIGHT".equalsIgnoreCase(user.getGoal().name())) {
                tdee += 300;
            }
        }

        int protein = (int) Math.round(weight * 2.0);
        int fat = (int) Math.round(weight * 1.0);
        int carbs = (tdee - (protein * 4) - (fat * 9)) / 4;

        if (carbs < 0) carbs = 0;

        return new NutritionTargetsDto(tdee, protein, fat, carbs);
    }
}