package com.example.demo.auth.dto;

import com.example.demo.auth.entity.Gender;

public record BiometricsUpdate(
        Double weight,
        Double height,
        Integer age,
        Gender gender
) {}
