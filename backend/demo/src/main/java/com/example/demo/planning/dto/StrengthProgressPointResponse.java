package com.example.demo.planning.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record StrengthProgressPointResponse(
        LocalDate date,
        String exerciseName,
        BigDecimal maxWeight) {
}
