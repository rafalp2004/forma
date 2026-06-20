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
        challenge.setStatus(resolveStatus(dto.endDate()));
        challenge = challengeRepository.save(challenge);

        addParticipant(challenge.getId(), creatorId);
        createFeedEntry(creatorId, FeedType.CHALLENGE_CREATED, challenge.getStartDate(), challenge.getEndDate(), challenge.getId(), challenge.getTitle());

        return toDto(challenge, creatorId);
    }

    @Transactional
    public ChallengeDto joinChallenge(Long challengeId, Long userId) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Challenge not found"));
        completeIfExpired(challenge);

        if (challenge.getStatus() != ChallengeStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Challenge is not active");
        }

        participantRepository.findByChallengeIdAndUserId(challengeId, userId).ifPresent(p -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Already participating in this challenge");
        });

        addParticipant(challengeId, userId);
        createFeedEntry(userId, FeedType.CHALLENGE_JOINED, challenge.getStartDate(), challenge.getEndDate(), challengeId, challenge.getTitle());

        return toDto(challenge, userId);
    }

    @Transactional
    public void leaveChallenge(Long challengeId, Long userId) {
        if (!challengeRepository.existsById(challengeId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Challenge not found");
        }
        participantRepository.findByChallengeIdAndUserId(challengeId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not participating in this challenge"));
        participantRepository.deleteByChallengeIdAndUserId(challengeId, userId);
    }

    @Transactional
    public List<ChallengeDto> getActiveChallenges(Long userId) {
        return challengeRepository.findByStatus(ChallengeStatus.ACTIVE)
                .stream()
                .peek(this::completeIfExpired)
                .filter(c -> c.getStatus() == ChallengeStatus.ACTIVE)
                .map(c -> toDto(c, userId))
                .toList();
    }

    @Transactional
    public ChallengeDto getChallengeById(Long id, Long userId) {
        return challengeRepository.findById(id)
                .map(this::completeIfExpired)
                .map(c -> toDto(c, userId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Challenge not found"));
    }

    @Transactional
    public List<LeaderboardEntryDto> getLeaderboard(Long challengeId) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Challenge not found"));
        completeIfExpired(challenge);

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

    private ChallengeStatus resolveStatus(LocalDate endDate) {
        return endDate.isBefore(LocalDate.now()) ? ChallengeStatus.COMPLETED : ChallengeStatus.ACTIVE;
    }

    private Challenge completeIfExpired(Challenge challenge) {
        if (challenge.getStatus() == ChallengeStatus.ACTIVE && challenge.getEndDate().isBefore(LocalDate.now())) {
            challenge.setStatus(ChallengeStatus.COMPLETED);
        }
        return challenge;
    }

    private void createFeedEntry(Long userId, FeedType type, LocalDate startDate, LocalDate endDate,
                                  Long challengeId, String challengeTitle) {
        ActivityFeed feed = new ActivityFeed();
        feed.setUserId(userId);
        feed.setType(type);
        feed.setStartDate(startDate);
        feed.setEndDate(endDate);
        feed.setChallengeId(challengeId);
        feed.setChallengeTitle(challengeTitle);
        feedRepository.save(feed);
    }

    private ChallengeDto toDto(Challenge c, Long userId) {
        int count = participantRepository.countByChallengeId(c.getId());
        boolean isParticipant = participantRepository.findByChallengeIdAndUserId(c.getId(), userId).isPresent();
        return new ChallengeDto(
                c.getId(), c.getCreatorId(), c.getTitle(), c.getDescription(),
                c.getStatus().name(), c.getStartDate(), c.getEndDate(),
                c.getMetric().name(), c.getCreatedAt(), count, isParticipant
        );
    }
}
