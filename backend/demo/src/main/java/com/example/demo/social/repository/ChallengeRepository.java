package com.example.demo.social.repository;

import com.example.demo.social.entity.Challenge;
import com.example.demo.social.entity.ChallengeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    List<Challenge> findByStatus(ChallengeStatus status);
}
