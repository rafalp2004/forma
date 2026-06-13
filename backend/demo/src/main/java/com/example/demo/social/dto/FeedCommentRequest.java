package com.example.demo.social.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FeedCommentRequest(
        @NotBlank
        @Size(max = 500)
        String content
) {}
