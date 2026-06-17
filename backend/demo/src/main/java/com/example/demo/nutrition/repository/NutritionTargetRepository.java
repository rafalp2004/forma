package com.example.demo.nutrition.repository;

import com.example.demo.nutrition.model.NutritionTarget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NutritionTargetRepository extends JpaRepository<NutritionTarget, Long> {
    Optional<NutritionTarget> findByUserId(Long userId);
}