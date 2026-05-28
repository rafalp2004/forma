package com.example.demo.social.service;

import com.example.demo.shared.dto.UserDto;
import com.example.demo.shared.services.UserQueryService;
import com.example.demo.social.dto.FeedEntryDto;
import com.example.demo.social.entity.FriendshipStatus;
import com.example.demo.social.repository.ActivityFeedRepository;
import com.example.demo.social.repository.FriendshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final ActivityFeedRepository feedRepository;
    private final FriendshipRepository friendshipRepository;
    private final UserQueryService userQueryService;

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
                    return new FeedEntryDto(
                            af.getId(), af.getUserId(), user.username(),
                            af.getType().name(), af.getStartDate(), af.getEndDate(), af.getCreatedAt()
                    );
                })
                .toList();
    }
}
