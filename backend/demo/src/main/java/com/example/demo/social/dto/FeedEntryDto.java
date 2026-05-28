package com.example.demo.social.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record FeedEntryDto(
        Long id,
        Long userId,
        String username,
        String type,
        LocalDate startDate,
        LocalDate endDate,
        LocalDateTime createdAt
) {}
