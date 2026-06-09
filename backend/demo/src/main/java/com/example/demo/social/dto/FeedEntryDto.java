package com.example.demo.social.dto;

import com.example.demo.shared.dto.WorkoutSummaryDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record FeedEntryDto(
        Long id,
        Long userId,
        String username,
        String type,
        LocalDate startDate,
        LocalDate endDate,
        LocalDateTime createdAt,
        Long challengeId,
        String challengeTitle,
        WorkoutSummaryDto workoutSummary,
        List<FeedCommentDto> comments
) {}
