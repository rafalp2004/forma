package com.example.demo.planning.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record WeightEntryRequest(
        @NotNull
        Long userId,

        @NotNull
        LocalDate date,

        @NotNull
        @DecimalMin(value = "0.1")
        BigDecimal weightKg
) {
}
