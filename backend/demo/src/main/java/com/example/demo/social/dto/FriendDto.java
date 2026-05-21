package com.example.demo.social.dto;

public record FriendDto(
        Long friendshipId,
        Long userId,
        String username,
        String email,
        String status
) {}
