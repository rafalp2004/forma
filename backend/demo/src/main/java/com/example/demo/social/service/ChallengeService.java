package com.example.demo.social.service;

import com.example.demo.shared.dto.UserDto;
import com.example.demo.shared.dto.WorkoutSummaryDto;
import com.example.demo.shared.services.UserQueryService;
import com.example.demo.shared.services.WorkoutQueryService;
import com.example.demo.social.dto.ChallengeCreateDto;
import com.example.demo.social.dto.ChallengeDto;
import com.example.demo.social.dto.LeaderboardEntryDto;
import com.example.demo.social.entity.*;
import com.example.demo.social.repository.ActivityFeedRepository;
import com.example.demo.social.repository.ChallengeParticipantRepository;
import com.example.demo.social.repository.ChallengeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final ChallengeParticipantRepository participantRepository;
    private final ActivityFeedRepository feedRepository;
    private final WorkoutQueryService workoutQueryService;
    private final UserQueryService userQueryService;
    private final ScoreCalculationService scoreCalculationService;

    @Transactional
    public ChallengeDto createChallenge(Long creatorId, ChallengeCreateDto dto) {
        Challenge challenge = new Challenge();
        challenge.setCreatorId(creatorId);
        challenge.setTitle(dto.title());
        challenge.setDescription(dto.description());
        challenge.setStartDate(dto.startDate());
        challenge.setEndDate(dto.endDate());
        try {
            challenge.setMetric(ChallengeMetric.valueOf(dto.metric().toUpperCase()));
        } catch (IllegalArgumentException e) {
            String allowed = Arrays.stream(ChallengeMetric.values())
                    .map(Enum::name)
                    .collect(Collectors.joining(", "));
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Nieprawidłowa metryka: '" + dto.metric() + "'. Dozwolone wartości: " + allowed);
        }
        challenge.setStatus(ChallengeStatus.ACTIVE);
        challenge = challengeRepository.save(challenge);

        addParticipant(challenge.getId(), creatorId);
        createFeedEntry(creatorId, FeedType.CHALLENGE_CREATED, challenge.getStartDate(), challenge.getEndDate());

        return toDto(challenge);
    }

    @Transactional
    public ChallengeDto joinChallenge(Long challengeId, Long userId) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Challenge not found"));

        if (challenge.getStatus() != ChallengeStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Challenge is not active");
        }

        participantRepository.findByChallengeIdAndUserId(challengeId, userId).ifPresent(p -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Already participating in this challenge");
        });

        addParticipant(challengeId, userId);
        createFeedEntry(userId, FeedType.CHALLENGE_JOINED, challenge.getStartDate(), challenge.getEndDate());

        return toDto(challenge);
    }

    public List<ChallengeDto> getActiveChallenges() {
        return challengeRepository.findByStatus(ChallengeStatus.ACTIVE)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public ChallengeDto getChallengeById(Long id) {
        return challengeRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Challenge not found"));
    }

    @Transactional
    public List<LeaderboardEntryDto> getLeaderboard(Long challengeId) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Challenge not found"));

        List<ChallengeParticipant> participants = participantRepository.findByChallengeIdOrderByScoreDesc(challengeId);
        refreshScores(challenge, participants);

        List<ChallengeParticipant> ranked = participantRepository.findByChallengeIdOrderByScoreDesc(challengeId);
        List<LeaderboardEntryDto> leaderboard = new ArrayList<>();
        for (int i = 0; i < ranked.size(); i++) {
            ChallengeParticipant p = ranked.get(i);
            UserDto user = userQueryService.findById(p.getUserId());
            leaderboard.add(new LeaderboardEntryDto(i + 1, user.id(), user.username(), p.getScore()));
        }
        return leaderboard;
    }

    private void addParticipant(Long challengeId, Long userId) {
        ChallengeParticipant participant = new ChallengeParticipant();
        participant.setChallengeId(challengeId);
        participant.setUserId(userId);
        participant.setScore(0.0);
        participantRepository.save(participant);
    }

    private void refreshScores(Challenge challenge, List<ChallengeParticipant> participants) {
        for (ChallengeParticipant p : participants) {
            List<WorkoutSummaryDto> history = workoutQueryService.getHistory(
                    p.getUserId(), challenge.getStartDate(), challenge.getEndDate());
            double score = scoreCalculationService.calculate(history, challenge.getMetric());
            p.setScore(score);
        }
        participantRepository.saveAll(participants);
    }

    private void createFeedEntry(Long userId, FeedType type, LocalDate startDate, LocalDate endDate) {
        ActivityFeed feed = new ActivityFeed();
        feed.setUserId(userId);
        feed.setType(type);
        feed.setStartDate(startDate);
        feed.setEndDate(endDate);
        feedRepository.save(feed);
    }

    private ChallengeDto toDto(Challenge c) {
        int count = participantRepository.countByChallengeId(c.getId());
        return new ChallengeDto(
                c.getId(), c.getCreatorId(), c.getTitle(), c.getDescription(),
                c.getStatus().name(), c.getStartDate(), c.getEndDate(),
                c.getMetric().name(), c.getCreatedAt(), count
        );
    }
}
