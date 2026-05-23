package com.example.demo.planning.repository;

import com.example.demo.planning.entity.WeightEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WeightEntryRepository extends JpaRepository<WeightEntry, Long> {

    List<WeightEntry> findByUserIdOrderByDateAsc(Long userId);

    Optional<WeightEntry> findByUserIdAndDate(
            Long userId,
            LocalDate date
    );

    List<WeightEntry> findByUserIdAndDateBetween(
            Long userId,
            LocalDate from,
            LocalDate to
    );
}
