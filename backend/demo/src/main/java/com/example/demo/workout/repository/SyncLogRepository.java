package com.example.demo.workout.repository;

import com.example.demo.workout.entity.SyncLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SyncLogRepository extends JpaRepository<SyncLog, Long> {
    Optional<SyncLog> findFirstByStatusOrderByLastSyncDateDesc(String status);
}