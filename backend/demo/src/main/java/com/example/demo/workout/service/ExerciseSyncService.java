package com.example.demo.workout.service;

import com.example.demo.workout.entity.Exercise;
import com.example.demo.workout.entity.SyncLog;
import com.example.demo.workout.repository.ExerciseRepository;
import com.example.demo.workout.repository.SyncLogRepository;
import com.example.demo.workout.client.ExerciseDBClient;
import com.example.demo.workout.client.ExerciseDBClient.ExternalExerciseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExerciseSyncService {

    private final ExerciseRepository exerciseRepository;
    private final SyncLogRepository syncLogRepository;
    private final ExerciseDBClient exerciseDBClient;

    @Transactional
    public String performSync() {
        log.info("Rozpoczęcie procesu synchronizacji bazy ćwiczeń.");

        long count = exerciseRepository.count();
        if (count > 0) {
            log.info("Baza ćwiczeń jest aktualna. Liczba rekordów: {}", count);
            return "Cache aktualny, w bazie jest już " + count + " ćwiczeń.";
        }

        try {
            List<ExternalExerciseDto> apiExercises = exerciseDBClient.fetchExercises();
            List<Exercise> exercisesToSave = new ArrayList<>();

            for (ExternalExerciseDto ext : apiExercises) {
                Exercise exercise = new Exercise();
                exercise.setId(ext.exerciseId());
                exercise.setName(ext.name());
                exercise.setGifUrl(ext.gifUrl());

                String muscle = ext.targetMuscles() != null && !ext.targetMuscles().isEmpty()
                        ? ext.targetMuscles().get(0)
                        : "unknown";

                String equip = ext.equipments() != null && !ext.equipments().isEmpty()
                        ? ext.equipments().get(0)
                        : "body weight";

                exercise.setMuscleGroup(muscle);
                exercise.setEquipment(equip);

                if (ext.instructions() != null) {
                    exercise.setInstructions(String.join("\n", ext.instructions()));
                } else {
                    exercise.setInstructions("");
                }

                exercisesToSave.add(exercise);
            }

            exerciseRepository.saveAll(exercisesToSave);
            syncLogRepository.save(new SyncLog(null, LocalDateTime.now(), "SUCCESS"));
            log.info("Synchronizacja zakończona sukcesem. Zapisano {} ćwiczeń.", exercisesToSave.size());

            return "Sukces! Pomyślnie zaciągnięto " + exercisesToSave.size() + " ćwiczeń.";

        } catch (Exception e) {
            log.error("Błąd podczas synchronizacji ćwiczeń: ", e);
            syncLogRepository.save(new SyncLog(null, LocalDateTime.now(), "FAILED"));
            throw new RuntimeException("Błąd synchronizacji: " + e.getMessage());
        }
    }
}