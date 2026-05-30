package com.example.demo.social.controller;

import com.example.demo.auth.entity.User;
import com.example.demo.auth.security.CurrentUser;
import com.example.demo.social.dto.FeedCommentDto;
import com.example.demo.social.dto.FeedCommentRequest;
import com.example.demo.social.dto.FeedEntryDto;
import com.example.demo.social.service.FeedCommentService;
import com.example.demo.social.service.FeedService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;
    private final FeedCommentService feedCommentService;

    @GetMapping
    public List<FeedEntryDto> getFeed(@CurrentUser User currentUser) {
        return feedService.getFeed(currentUser.getId());
    }

    @PostMapping("/{id}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public FeedCommentDto addComment(@PathVariable Long id,
                                     @CurrentUser User currentUser,
                                     @Valid @RequestBody FeedCommentRequest request) {
        return feedCommentService.addComment(id, currentUser.getId(), request);
    }

    @GetMapping("/{id}/comments")
    public List<FeedCommentDto> getComments(@PathVariable Long id) {
        return feedCommentService.getComments(id);
    }

    @DeleteMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long commentId, @CurrentUser User currentUser) {
        feedCommentService.deleteComment(commentId, currentUser.getId());
    }
}
