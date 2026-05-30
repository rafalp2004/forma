package com.example.demo.social.repository;

import com.example.demo.social.entity.ChallengeParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChallengeParticipantRepository extends JpaRepository<ChallengeParticipant, Long> {

    List<ChallengeParticipant> findByChallengeIdOrderByScoreDesc(Long challengeId);

    Optional<ChallengeParticipant> findByChallengeIdAndUserId(Long challengeId, Long userId);

    int countByChallengeId(Long challengeId);

    long countByUserId(Long userId);
}
