package com.example.demo.social.dto;

import java.time.LocalDateTime;

public record FeedCommentDto(
        Long id,
        Long feedEntryId,
        Long userId,
        String username,
        String content,
        LocalDateTime createdAt
) {}
