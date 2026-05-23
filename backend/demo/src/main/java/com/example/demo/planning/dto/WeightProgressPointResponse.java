package com.example.demo.planning.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record WeightProgressPointResponse(
        LocalDate date,
        BigDecimal weightKg
) {
}
