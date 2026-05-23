package com.example.demo.workout.controller;

import com.example.demo.shared.dto.WorkoutSummaryDto;
import com.example.demo.shared.services.WorkoutQueryService;
import com.example.demo.workout.dto.WorkoutSessionDto;
import com.example.demo.workout.service.WorkoutCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/workouts")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class WorkoutController {

    private final WorkoutQueryService workoutQueryService;
    private final WorkoutCommandService workoutCommandService;

    @GetMapping("/history")
    public ResponseEntity<List<WorkoutSummaryDto>> getHistory(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        return ResponseEntity.ok(workoutQueryService.getHistory(userId, from, to));
    }

    @PostMapping
    public ResponseEntity<String> createWorkout(@Valid @RequestBody WorkoutSessionDto dto) {
        workoutCommandService.saveWorkoutSession(dto);
        return ResponseEntity.ok("Trening został pomyślnie zapisany!");
    }
}