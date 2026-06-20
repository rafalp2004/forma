package com.example.demo.social.repository;

import com.example.demo.social.entity.ActivityFeed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ActivityFeedRepository extends JpaRepository<ActivityFeed, Long> {

    @Query("SELECT af FROM ActivityFeed af WHERE af.userId IN :userIds ORDER BY af.createdAt DESC")
    List<ActivityFeed> findFeedForUsers(@Param("userIds") List<Long> userIds);
}
