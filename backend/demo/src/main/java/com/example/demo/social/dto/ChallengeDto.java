package com.example.demo.social.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ChallengeDto(
        Long id,
        Long creatorId,
        String title,
        String description,
        String status,
        LocalDate startDate,
        LocalDate endDate,
        String metric,
        LocalDateTime createdAt,
        Integer participantCount
) {}
