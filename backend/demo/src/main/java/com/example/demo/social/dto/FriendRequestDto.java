package com.example.demo.social.dto;

import jakarta.validation.constraints.NotNull;

public record FriendRequestDto(
        @NotNull Long addresseeId
) {}
