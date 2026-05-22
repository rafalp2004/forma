package com.example.demo.social.controller;

import com.example.demo.social.dto.ChallengeCreateDto;
import com.example.demo.social.dto.ChallengeDto;
import com.example.demo.social.dto.LeaderboardEntryDto;
import com.example.demo.auth.security.CurrentUser;
import com.example.demo.social.service.ChallengeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/challenges")
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService challengeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ChallengeDto createChallenge(@CurrentUser Long currentUserId,
                                        @Valid @RequestBody ChallengeCreateDto dto) {
        return challengeService.createChallenge(currentUserId, dto);
    }

    @PostMapping("/{id}/join")
    public ChallengeDto joinChallenge(@CurrentUser Long currentUserId,
                                      @PathVariable Long id) {
        return challengeService.joinChallenge(id, currentUserId);
    }

    @GetMapping
    public List<ChallengeDto> getActiveChallenges() {
        return challengeService.getActiveChallenges();
    }

    @GetMapping("/{id}")
    public ChallengeDto getChallengeById(@PathVariable Long id) {
        return challengeService.getChallengeById(id);
    }

    @GetMapping("/{id}/leaderboard")
    public List<LeaderboardEntryDto> getLeaderboard(@PathVariable Long id) {
        return challengeService.getLeaderboard(id);
    }
}
