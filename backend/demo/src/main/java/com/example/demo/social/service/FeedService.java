package com.example.demo.social.service;

import com.example.demo.shared.dto.UserDto;
import com.example.demo.shared.dto.WorkoutSummaryDto;
import com.example.demo.shared.services.UserQueryService;
import com.example.demo.shared.services.WorkoutQueryService;
import com.example.demo.social.dto.FeedCommentDto;
import com.example.demo.social.dto.FeedEntryDto;
import com.example.demo.social.entity.FeedComment;
import com.example.demo.social.entity.FriendshipStatus;
import com.example.demo.social.repository.ActivityFeedRepository;
import com.example.demo.social.repository.FeedCommentRepository;
import com.example.demo.social.repository.FriendshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final ActivityFeedRepository feedRepository;
    private final FriendshipRepository friendshipRepository;
    private final FeedCommentRepository feedCommentRepository;
    private final UserQueryService userQueryService;
    private final WorkoutQueryService workoutQueryService;

    public List<FeedEntryDto> getFeed(Long userId) {
        List<Long> friendIds = friendshipRepository.findAllByUserIdAndStatus(userId, FriendshipStatus.ACCEPTED)
                .stream()
                .map(f -> f.getRequesterId().equals(userId) ? f.getAddresseeId() : f.getRequesterId())
                .toList();

        if (friendIds.isEmpty()) {
            return List.of();
        }

        return feedRepository.findFeedForFriends(friendIds)
                .stream()
                .map(af -> {
                    UserDto user = userQueryService.findById(af.getUserId());
                    List<FeedCommentDto> comments = feedCommentRepository
                            .findByFeedEntryIdOrderByCreatedAtAsc(af.getId())
                            .stream()
                            .map(c -> {
                                UserDto commentUser = userQueryService.findById(c.getUserId());
                                return new FeedCommentDto(c.getId(), c.getFeedEntryId(), c.getUserId(),
                                        commentUser.username(), c.getContent(), c.getCreatedAt());
                            })
                            .toList();

                    WorkoutSummaryDto workoutSummary = af.getWorkoutSessionId() != null
                            ? workoutQueryService.getById(af.getWorkoutSessionId()).orElse(null)
                            : null;

                    return new FeedEntryDto(
                            af.getId(), af.getUserId(), user.username(),
                            af.getType().name(), af.getStartDate(), af.getEndDate(), af.getCreatedAt(),
                            af.getChallengeId(), af.getChallengeTitle(), workoutSummary, comments
                    );
                })
                .toList();
    }
}
