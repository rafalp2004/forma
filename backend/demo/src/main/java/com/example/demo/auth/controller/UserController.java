package com.example.demo.auth.controller;

import com.example.demo.auth.dto.BiometricsUpdate;
import com.example.demo.auth.dto.GoalUpdate;
import com.example.demo.auth.dto.UserDetailsResponse;
import com.example.demo.shared.dto.UserDto;
import com.example.demo.auth.entity.User;
import com.example.demo.auth.service.UserProfileService;
import com.example.demo.auth.service.UserQueryServiceImpl;
import com.example.demo.auth.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserProfileService userProfileService;
    private final UserQueryServiceImpl userQueryService;

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Long id) {
        return userQueryService.findById(id);
    }

    @GetMapping("/search")
    public List<UserDto> searchUsers(@RequestParam String query) {
        return userQueryService.searchByUsername(query);
    }

    @GetMapping("/me")
    public UserDetailsResponse getCurrentUser(@CurrentUser User user) {
        return userProfileService.getUserProfile(user.getId());
    }

    @PutMapping("/me/biometrics")
    public void updateBiometrics(@CurrentUser User user, @RequestBody BiometricsUpdate update) {
        userProfileService.updateBiometrics(user.getId(), update);
    }

    @PutMapping("/me/goals")
    public void updateGoals(@CurrentUser User user, @RequestBody GoalUpdate update) {
        userProfileService.updateGoals(user.getId(), update);
    }
}
