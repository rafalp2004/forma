package com.example.demo.auth.service;

import com.example.demo.auth.dto.BiometricsUpdate;
import com.example.demo.auth.dto.GoalUpdate;
import com.example.demo.auth.dto.UserDetailsResponse;
import com.example.demo.shared.dto.UserDto;
import com.example.demo.auth.entity.User;
import com.example.demo.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;

    public UserDetailsResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return new UserDetailsResponse(user.getUsername(), user.getEmail(), user.getAge(), user.getWeight(), user.getHeight(), user.getGender(), user.getGoal(), user.getCreatedAt(), user.getUpdatedAt());
    }

    @Transactional
    public void updateBiometrics(Long userId, BiometricsUpdate update) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setWeight(update.weight());
        user.setHeight(update.height());
        user.setAge(update.age());
        user.setGender(update.gender());
        userRepository.save(user);
    }

    @Transactional
    public void updateGoals(Long userId, GoalUpdate update) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setGoal(update.goal());
        userRepository.save(user);
    }
}
