package com.example.demo.social.service;

import com.example.demo.shared.dto.UserDto;
import com.example.demo.shared.services.UserQueryService;
import com.example.demo.social.dto.FriendDto;
import com.example.demo.social.dto.FriendRequestDto;
import com.example.demo.social.entity.Friendship;
import com.example.demo.social.entity.FriendshipStatus;
import com.example.demo.social.repository.FriendshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final UserQueryService userQueryService;

    public FriendDto sendFriendRequest(Long requesterId, FriendRequestDto dto) {
        Long addresseeId = dto.addresseeId();

        if (requesterId.equals(addresseeId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot send friend request to yourself");
        }

        friendshipRepository.findBetweenUsers(requesterId, addresseeId).ifPresent(f -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Friendship already exists");
        });

        Friendship friendship = new Friendship();
        friendship.setUserId(requesterId);
        friendship.setRequesterId(requesterId);
        friendship.setAddresseeId(addresseeId);
        friendship.setStatus(FriendshipStatus.PENDING);
        friendship = friendshipRepository.save(friendship);

        UserDto addressee = userQueryService.findById(addresseeId);
        return toDto(friendship, addresseeId, addressee);
    }

    public FriendDto acceptFriendRequest(Long friendshipId, Long currentUserId) {
        Friendship friendship = findPendingForAddressee(friendshipId, currentUserId);
        friendship.setStatus(FriendshipStatus.ACCEPTED);
        friendship = friendshipRepository.save(friendship);

        UserDto requester = userQueryService.findById(friendship.getRequesterId());
        return toDto(friendship, friendship.getRequesterId(), requester);
    }

    public void rejectFriendRequest(Long friendshipId, Long currentUserId) {
        Friendship friendship = findPendingForAddressee(friendshipId, currentUserId);
        friendship.setStatus(FriendshipStatus.REJECTED);
        friendshipRepository.save(friendship);
    }

    public List<FriendDto> getFriends(Long userId) {
        return friendshipRepository.findAllByUserIdAndStatus(userId, FriendshipStatus.ACCEPTED)
                .stream()
                .map(f -> {
                    Long friendId = f.getRequesterId().equals(userId) ? f.getAddresseeId() : f.getRequesterId();
                    UserDto user = userQueryService.findById(friendId);
                    return toDto(f, friendId, user);
                })
                .toList();
    }

    public List<FriendDto> getPendingRequests(Long currentUserId) {
        return friendshipRepository.findByAddresseeIdAndStatus(currentUserId, FriendshipStatus.PENDING)
                .stream()
                .map(f -> {
                    UserDto requester = userQueryService.findById(f.getRequesterId());
                    return toDto(f, f.getRequesterId(), requester);
                })
                .toList();
    }

    private Friendship findPendingForAddressee(Long friendshipId, Long currentUserId) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Friend request not found"));

        if (!friendship.getAddresseeId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized");
        }
        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request is not pending");
        }
        return friendship;
    }

    private FriendDto toDto(Friendship f, Long friendUserId, UserDto user) {
        return new FriendDto(f.getId(), user.id(), user.username(), user.email(), f.getStatus().name());
    }
}
