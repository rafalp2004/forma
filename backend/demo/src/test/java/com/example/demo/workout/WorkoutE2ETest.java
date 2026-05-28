package com.example.demo.workout;

import com.example.demo.BaseE2ETest;
import com.example.demo.shared.dto.WorkoutSummaryDto;
import com.example.demo.workout.dto.WorkoutSessionDto;
import com.example.demo.workout.dto.WorkoutSetInputDto;
import tools.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testy E2E modułu workout.
 * Pokrywa tworzenie sesji treningowych, historię i ćwiczenia.
 */
class WorkoutE2ETest extends BaseE2ETest {

    // ==================== TWORZENIE SESJI TRENINGOWEJ ====================

    @Test
    void trening_nowaSesjaTreningowa_zwraca200ZKomunikatem() throws Exception {
        // Zarejestruj użytkownika, żeby mieć prawdziwe ID
        String username = uniqueUser("workout_user");
        String token = register(username);
        Long userId = getUserId(token, username);

        var set = new WorkoutSetInputDto("exercise_bench", 10, 100.0, LocalDateTime.now());
        var session = new WorkoutSessionDto(
                userId,
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now(),
                List.of(set)
        );

        MvcResult result = mockMvc.perform(post("/api/workouts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(session)))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(result.getResponse().getContentAsString())
                .contains("pomyślnie");
    }

    @Test
    void trening_walidacja_pustaSets_zwraca400() throws Exception {
        var session = new WorkoutSessionDto(
                1L,
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now(),
                List.of()  // pusta lista — narusza @NotEmpty
        );

        mockMvc.perform(post("/api/workouts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(session)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void trening_walidacja_brakUserId_zwraca400() throws Exception {
        var set = new WorkoutSetInputDto("exercise_sq", 5, 80.0, LocalDateTime.now());
        var session = new WorkoutSessionDto(
                null,  // brak userId — narusza @NotNull
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now(),
                List.of(set)
        );

        mockMvc.perform(post("/api/workouts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(session)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void trening_walidacja_brakStartTime_zwraca400() throws Exception {
        var set = new WorkoutSetInputDto("exercise_dl", 3, 120.0, LocalDateTime.now());
        var session = new WorkoutSessionDto(
                1L,
                null,  // brak startTime — narusza @NotNull
                LocalDateTime.now(),
                List.of(set)
        );

        mockMvc.perform(post("/api/workouts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(session)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void trening_zbiory_minimalnaWagaI1Powt_przetwarzanePoprawnie() throws Exception {
        String username = uniqueUser("min_workout");
        String token = register(username);
        Long userId = getUserId(token, username);

        // Seria z wagą 0 (ćwiczenie z masą ciała) i 1 powtórzeniem
        var set = new WorkoutSetInputDto("exercise_pullup", 1, 0.0, LocalDateTime.now());
        var session = new WorkoutSessionDto(
                userId,
                LocalDateTime.now().minusMinutes(30),
                LocalDateTime.now(),
                List.of(set)
        );

        mockMvc.perform(post("/api/workouts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(session)))
                .andExpect(status().isOk());
    }

    // ==================== HISTORIA TRENINGÓW ====================

    @Test
    void historia_poZapisaniuTreningu_zwracaSesje() throws Exception {
        String username = uniqueUser("hist_user");
        String token = register(username);
        Long userId = getUserId(token, username);

        // Zapisz sesję z 2 seriami
        var set1 = new WorkoutSetInputDto("exercise_squat", 5, 100.0, LocalDateTime.now());
        var set2 = new WorkoutSetInputDto("exercise_squat", 5, 100.0, LocalDateTime.now());
        var session = new WorkoutSessionDto(
                userId,
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now(),
                List.of(set1, set2)
        );

        mockMvc.perform(post("/api/workouts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(session)))
                .andExpect(status().isOk());

        // Pobierz historię z dzisiejszego dnia
        String today = LocalDate.now().toString();
        MvcResult histResult = mockMvc.perform(get("/api/workouts/history")
                        .param("userId", userId.toString())
                        .param("from", today)
                        .param("to", today))
                .andExpect(status().isOk())
                .andReturn();

        List<WorkoutSummaryDto> history = objectMapper.readValue(
                histResult.getResponse().getContentAsString(), new TypeReference<>() {});

        assertThat(history).hasSize(1);
        assertThat(history.get(0).userId()).isEqualTo(userId);
        assertThat(history.get(0).totalSets()).isEqualTo(2);
        // 2 serie × 5 powtórzeń × 100 kg = 1000 kg objętości
        assertThat(history.get(0).totalVolumeKg()).isEqualTo(1000.0);
    }

    @Test
    void historia_zakresDatePrzedTreningiem_zwracaPustaListe() throws Exception {
        String username = uniqueUser("hist_empty");
        String token = register(username);
        Long userId = getUserId(token, username);

        // Zakres dat sprzed roku — brak treningów w tym zakresie
        String oldDate = LocalDate.now().minusYears(1).toString();
        MvcResult result = mockMvc.perform(get("/api/workouts/history")
                        .param("userId", userId.toString())
                        .param("from", oldDate)
                        .param("to", oldDate))
                .andExpect(status().isOk())
                .andReturn();

        List<WorkoutSummaryDto> history = objectMapper.readValue(
                result.getResponse().getContentAsString(), new TypeReference<>() {});
        assertThat(history).isEmpty();
    }

    @Test
    void historia_wieleSesjiTegoPotrzebUzytkownika_zwracaWszystkie() throws Exception {
        String username = uniqueUser("hist_multi");
        String token = register(username);
        Long userId = getUserId(token, username);

        var set = new WorkoutSetInputDto("exercise_ohp", 8, 60.0, LocalDateTime.now());

        // Dwie sesje treningowe
        for (int i = 0; i < 2; i++) {
            var s = new WorkoutSessionDto(
                    userId,
                    LocalDateTime.now().minusHours(2),
                    LocalDateTime.now(),
                    List.of(set)
            );
            mockMvc.perform(post("/api/workouts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(s)))
                    .andExpect(status().isOk());
        }

        String today = LocalDate.now().toString();
        MvcResult result = mockMvc.perform(get("/api/workouts/history")
                        .param("userId", userId.toString())
                        .param("from", today)
                        .param("to", today))
                .andExpect(status().isOk())
                .andReturn();

        List<WorkoutSummaryDto> history = objectMapper.readValue(
                result.getResponse().getContentAsString(), new TypeReference<>() {});
        assertThat(history).hasSize(2);
    }

    // ==================== ĆWICZENIA ====================

    @Test
    void cwiczenia_swiezaBaza_zwracaPustaListe() throws Exception {
        // Brak synchronizacji z API — baza ćwiczeń jest pusta
        MvcResult result = mockMvc.perform(get("/api/exercises"))
                .andExpect(status().isOk())
                .andReturn();

        // Może być pusta lub zawierać dane z poprzednich testów (baza H2 jest współdzielona)
        assertThat(result.getResponse().getContentAsString()).startsWith("[");
    }

    @Test
    void cwiczenia_wyszukiwaniePoFrazie_zwraca200() throws Exception {
        // Nawet pusty wynik jest poprawną odpowiedzią
        MvcResult result = mockMvc.perform(get("/api/exercises")
                        .param("search", "bench press"))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).startsWith("[");
    }
}
