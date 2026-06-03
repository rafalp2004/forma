package com.example.demo.nutrition.repository;

import com.example.demo.nutrition.model.MealEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MealEntryRepository extends JpaRepository<MealEntry, Long> {
    List<MealEntry> findByUserIdAndConsumptionDate(Long userId, LocalDate consumptionDate);
}