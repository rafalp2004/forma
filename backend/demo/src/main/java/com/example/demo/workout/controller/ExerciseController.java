package com.example.demo.workout.controller;

import com.example.demo.shared.dto.ExerciseDto;
import com.example.demo.workout.service.ExerciseSyncService;
import com.example.demo.workout.service.WorkoutQueryServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exercises")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ExerciseController {

    private final WorkoutQueryServiceImpl workoutQueryService;
    private final ExerciseSyncService exerciseSyncService;

    @GetMapping
    public ResponseEntity<List<ExerciseDto>> getAllExercises(@RequestParam(required = false) String search) {
        if (search != null && !search.isBlank()) {
            return ResponseEntity.ok(workoutQueryService.searchExercises(search));
        }
        return ResponseEntity.ok(workoutQueryService.getAllExercises());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExerciseDto> getExerciseById(@PathVariable String id) {
        return ResponseEntity.ok(workoutQueryService.getExerciseById(id));
    }

    @PostMapping("/sync")
    public ResponseEntity<String> syncExercises() {
        return ResponseEntity.ok(exerciseSyncService.performSync());
    }
}