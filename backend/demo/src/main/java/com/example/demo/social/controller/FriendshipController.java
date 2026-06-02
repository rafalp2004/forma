package com.example.demo.social.controller;

import com.example.demo.auth.entity.User;
import com.example.demo.auth.security.CurrentUser;
import com.example.demo.social.dto.FriendDto;
import com.example.demo.social.dto.FriendRequestDto;
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
    public FriendDto sendFriendRequest(@CurrentUser User currentUser,
                                       @Valid @RequestBody FriendRequestDto dto) {
        return friendshipService.sendFriendRequest(currentUser.getId(), dto);
    }

    @PostMapping("/accept/{id}")
    public FriendDto acceptFriendRequest(@CurrentUser User currentUser,
                                         @PathVariable Long id) {
        return friendshipService.acceptFriendRequest(id, currentUser.getId());
    }

    @PostMapping("/reject/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void rejectFriendRequest(@CurrentUser User currentUser,
                                    @PathVariable Long id) {
        friendshipService.rejectFriendRequest(id, currentUser.getId());
    }

    @GetMapping
    public List<FriendDto> getFriends(@CurrentUser User currentUser) {
        return friendshipService.getFriends(currentUser.getId());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFriend(@CurrentUser User currentUser, @PathVariable Long id) {
        friendshipService.removeFriend(id, currentUser.getId());
    }

    @GetMapping("/pending")
    public List<FriendDto> getPendingRequests(@CurrentUser User currentUser) {
        return friendshipService.getPendingRequests(currentUser.getId());
    }
}
