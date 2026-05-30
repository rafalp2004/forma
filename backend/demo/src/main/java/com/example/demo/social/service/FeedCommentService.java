package com.example.demo.social.service;

import com.example.demo.shared.dto.UserDto;
import com.example.demo.shared.services.UserQueryService;
import com.example.demo.social.dto.FeedCommentDto;
import com.example.demo.social.dto.FeedCommentRequest;
import com.example.demo.social.entity.FeedComment;
import com.example.demo.social.repository.ActivityFeedRepository;
import com.example.demo.social.repository.FeedCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedCommentService {

    private final FeedCommentRepository feedCommentRepository;
    private final ActivityFeedRepository activityFeedRepository;
    private final UserQueryService userQueryService;

    public FeedCommentDto addComment(Long feedEntryId, Long userId, FeedCommentRequest request) {
        if (!activityFeedRepository.existsById(feedEntryId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Feed entry not found");
        }

        FeedComment comment = new FeedComment();
        comment.setFeedEntryId(feedEntryId);
        comment.setUserId(userId);
        comment.setContent(request.content());
        comment = feedCommentRepository.save(comment);

        UserDto user = userQueryService.findById(userId);
        return toDto(comment, user.username());
    }

    public void deleteComment(Long commentId, Long userId) {
        FeedComment comment = feedCommentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));
        if (!comment.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your comment");
        }
        feedCommentRepository.delete(comment);
    }

    public List<FeedCommentDto> getComments(Long feedEntryId) {
        return feedCommentRepository.findByFeedEntryIdOrderByCreatedAtAsc(feedEntryId)
                .stream()
                .map(c -> {
                    UserDto user = userQueryService.findById(c.getUserId());
                    return toDto(c, user.username());
                })
                .toList();
    }

    private FeedCommentDto toDto(FeedComment c, String username) {
        return new FeedCommentDto(c.getId(), c.getFeedEntryId(), c.getUserId(), username, c.getContent(), c.getCreatedAt());
    }
}
