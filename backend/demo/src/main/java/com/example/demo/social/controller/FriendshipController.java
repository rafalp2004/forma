package com.example.demo.social.controller;

import com.example.demo.social.dto.FriendDto;
import com.example.demo.social.dto.FriendRequestDto;
import com.example.demo.social.security.CurrentUser;
import com.example.demo.social.service.FriendshipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendshipController {

    private final FriendshipService friendshipService;

    @PostMapping("/request")
    @ResponseStatus(HttpStatus.CREATED)
    public FriendDto sendFriendRequest(@CurrentUser Long currentUserId,
                                       @Valid @RequestBody FriendRequestDto dto) {
        return friendshipService.sendFriendRequest(currentUserId, dto);
    }

    @PostMapping("/accept/{id}")
    public FriendDto acceptFriendRequest(@CurrentUser Long currentUserId,
                                         @PathVariable Long id) {
        return friendshipService.acceptFriendRequest(id, currentUserId);
    }

    @PostMapping("/reject/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void rejectFriendRequest(@CurrentUser Long currentUserId,
                                    @PathVariable Long id) {
        friendshipService.rejectFriendRequest(id, currentUserId);
    }

    @GetMapping
    public List<FriendDto> getFriends(@RequestParam Long userId) {
        return friendshipService.getFriends(userId);
    }

    @GetMapping("/pending")
    public List<FriendDto> getPendingRequests(@CurrentUser Long currentUserId) {
        return friendshipService.getPendingRequests(currentUserId);
    }
}
