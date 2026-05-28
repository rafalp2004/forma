package com.example.demo.workout.controller;

import com.example.demo.shared.dto.ExerciseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ExerciseControllerIntegrationTest {

    @Autowired
    private ExerciseController exerciseController;

    @Test
    void controllerLoads() {
        assertNotNull(exerciseController, "Kontroler nie został wstrzyknięty!");
    }

    @Test
    void testGetAllExercises_ShouldReturnOk() {
        ResponseEntity<List<ExerciseDto>> response = exerciseController.getAllExercises(null);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    void testSearchExercises_ShouldReturnOk() {
        ResponseEntity<List<ExerciseDto>> response = exerciseController.getAllExercises("chest");
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void testSyncExercises_ShouldHitInternetAndReturnSuccess() {
        ResponseEntity<String> response = exerciseController.syncExercises();

        assertEquals(200, response.getStatusCode().value());
    }
}