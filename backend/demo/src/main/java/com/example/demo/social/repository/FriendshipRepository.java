package com.example.demo.social.repository;

import com.example.demo.social.entity.Friendship;
import com.example.demo.social.entity.FriendshipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    List<Friendship> findByAddresseeIdAndStatus(Long addresseeId, FriendshipStatus status);

    @Query("SELECT f FROM Friendship f WHERE (f.requesterId = :userId OR f.addresseeId = :userId) AND f.status = :status")
    List<Friendship> findAllByUserIdAndStatus(@Param("userId") Long userId, @Param("status") FriendshipStatus status);

    @Query("SELECT f FROM Friendship f WHERE (f.requesterId = :u1 AND f.addresseeId = :u2) OR (f.requesterId = :u2 AND f.addresseeId = :u1)")
    Optional<Friendship> findBetweenUsers(@Param("u1") Long u1, @Param("u2") Long u2);
}
