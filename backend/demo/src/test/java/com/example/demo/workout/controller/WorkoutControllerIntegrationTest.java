package com.example.demo.workout.controller;

import com.example.demo.shared.dto.WorkoutSummaryDto;
import com.example.demo.workout.dto.WorkoutSessionDto;
import com.example.demo.workout.dto.WorkoutSetInputDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class WorkoutControllerIntegrationTest {

    @Autowired
    private WorkoutController workoutController;

    @Test
    void testGetHistory_ShouldReturnOkAndList() {
        ResponseEntity<List<WorkoutSummaryDto>> response = workoutController.getHistory(
                1L,
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2026, 5, 31)
        );

        assertEquals(200, response.getStatusCode().value(), "Oczekiwano statusu 200 OK przy pobieraniu historii");
        assertNotNull(response.getBody(), "Historia nie powinna być nullem (może być pusta lista)");
    }

    @Test
    void testCreateWorkout_ShouldReturnOk_WhenDataIsValid() {
        WorkoutSetInputDto set = new WorkoutSetInputDto(
                "0025",                 // exerciseId
                10,                     // reps
                60.0,                   // weight
                LocalDateTime.now()     // performedAt
        );


        WorkoutSessionDto sessionDto = new WorkoutSessionDto(
                1L,
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now(),
                List.of(set)
        );

        ResponseEntity<String> response = workoutController.createWorkout(sessionDto);

        assertEquals(200, response.getStatusCode().value(), "Zapis treningu powinien zwrócić 200 OK");
        assertEquals("Trening został pomyślnie zapisany!", response.getBody());
    }
}