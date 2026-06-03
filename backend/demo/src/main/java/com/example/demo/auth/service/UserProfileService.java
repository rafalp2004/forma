package com.example.demo.auth.service;

import com.example.demo.auth.dto.BiometricsUpdate;
import com.example.demo.auth.dto.GoalUpdate;
import com.example.demo.auth.dto.PersonalRecordDto;
import com.example.demo.auth.dto.UserDetailsResponse;
import com.example.demo.shared.dto.UserDto;
import com.example.demo.auth.entity.User;
import com.example.demo.auth.repository.UserRepository;
import com.example.demo.workout.entity.Exercise;
import com.example.demo.workout.entity.WorkoutSet;
import com.example.demo.workout.repository.ExerciseRepository;
import com.example.demo.workout.repository.WorkoutSessionRepository;
import com.example.demo.social.repository.ChallengeParticipantRepository;
import com.example.demo.workout.repository.WorkoutSetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;
    private final WorkoutSessionRepository workoutSessionRepository;
    private final ChallengeParticipantRepository challengeParticipantRepository;
    private final WorkoutSetRepository workoutSetRepository;
    private final ExerciseRepository exerciseRepository;

    public UserDetailsResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        long workoutCount = workoutSessionRepository.countByUserId(userId);
        long challengeCount = challengeParticipantRepository.countByUserId(userId);

        List<WorkoutSet> prSets = workoutSetRepository.findPersonalRecordsByUserId(userId);
        List<String> exerciseIds = prSets.stream().map(WorkoutSet::getExerciseId).distinct().toList();
        Map<String, String> exerciseNames = exerciseRepository.findAllById(exerciseIds).stream()
                .collect(Collectors.toMap(Exercise::getId, Exercise::getName));

        List<PersonalRecordDto> personalRecords = prSets.stream()
                .map(set -> new PersonalRecordDto(
                        exerciseNames.getOrDefault(set.getExerciseId(), "Unknown Exercise"),
                        set.getWeight(),
                        set.getPerformedAt()
                ))
                .sorted((a, b) -> b.getWeight().compareTo(a.getWeight()))
                .limit(3)
                .toList();

        return new UserDetailsResponse(
                user.getUsername(),
                user.getEmail(),
                user.getAge(),
                user.getWeight(),
                user.getHeight(),
                user.getGender(),
                user.getGoal(),
                user.getTargetWeight(),
                user.getSessionsPerWeek(),
                user.getTargetKcal(),
                user.getTargetProtein(),
                user.getTargetFat(),
                user.getTargetCarbs(),
                user.getUseManualTargets(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                workoutCount,
                challengeCount,
                personalRecords
        );
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
        user.setTargetWeight(update.targetWeight());
        user.setSessionsPerWeek(update.sessionsPerWeek());
        userRepository.save(user);
    }
}
