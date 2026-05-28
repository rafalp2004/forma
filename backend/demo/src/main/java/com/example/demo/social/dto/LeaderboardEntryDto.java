package com.example.demo.social.dto;

public record LeaderboardEntryDto(
        Integer rank,
        Long userId,
        String username,
        Double score
) {}
