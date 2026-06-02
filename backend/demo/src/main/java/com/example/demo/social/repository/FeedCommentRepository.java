package com.example.demo.social.repository;

import com.example.demo.social.entity.FeedComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedCommentRepository extends JpaRepository<FeedComment, Long> {

    List<FeedComment> findByFeedEntryIdOrderByCreatedAtAsc(Long feedEntryId);
}
