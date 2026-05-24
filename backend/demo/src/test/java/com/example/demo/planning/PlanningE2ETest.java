package com.example.demo.planning;

import com.example.demo.BaseE2ETest;
import com.example.demo.planning.dto.PlanExerciseRequest;
import com.example.demo.planning.dto.TrainingPlanRequest;
import com.example.demo.planning.dto.TrainingPlanResponse;
import com.example.demo.planning.dto.WeightEntryRequest;
import com.example.demo.planning.dto.WeightProgressPointResponse;
import tools.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testy E2E modułu planowania.
 *
 * Uwaga: PlanningController jest zdefiniowany jako @RestController("/api"),
 * gdzie "/api" to nazwa beana, NIE prefix URL. Faktyczne ścieżki to:
 *   POST   /plans              — tworzenie planu
 *   GET    /plans              — lista planów użytkownika
 *   GET    /plans/{id}         — pojedynczy plan
 *   PUT    /api/plans/{id}     — aktualizacja planu
 *   DELETE /api/plans/{id}     — usuwanie planu
 *   POST   /api/stats/weight   — zapis wagi
 *   GET    /api/stats/weight   — historia wagi
 *   GET    /api/stats/progress — progres siłowy
 *   GET    /api/calendar       — kalendarz treningów
 */
class PlanningE2ETest extends BaseE2ETest {

    // ==================== PLANY TRENINGOWE — CRUD ====================

    @Test
    void plan_tworzenie_zwraca201IStatusDraft() throws Exception {
        String username = uniqueUser("plan_create");
        String token = register(username);

        var request = new TrainingPlanRequest(
                "Plan siłowy",
                "Opis planu",
                LocalDate.now(),
                LocalDate.now().plusDays(30),
                null
        );

        MvcResult result = authPost("/plans", token, request, 201);
        TrainingPlanResponse plan = objectMapper.readValue(
                result.getResponse().getContentAsString(), TrainingPlanResponse.class);

        assertThat(plan.id()).isNotNull();
        assertThat(plan.name()).isEqualTo("Plan siłowy");
        assertThat(plan.status()).isEqualTo("DRAFT");
        assertThat(plan.exercises()).isEmpty();
    }

    @Test
    void plan_tworzenieSCwiczeniami_cwiczeniaZapisane() throws Exception {
        String username = uniqueUser("plan_ex");
        String token = register(username);

        var exercise = new PlanExerciseRequest(
                "exercise_bench", "Bench Press", 1, 4, 8, new BigDecimal("100.0")
        );
        var request = new TrainingPlanRequest(
                "Plan klatki",
                null,
                LocalDate.now(),
                LocalDate.now().plusDays(14),
                List.of(exercise)
        );

        MvcResult result = authPost("/plans", token, request, 201);
        TrainingPlanResponse plan = objectMapper.readValue(
                result.getResponse().getContentAsString(), TrainingPlanResponse.class);

        assertThat(plan.exercises()).hasSize(1);
        assertThat(plan.exercises().get(0).exerciseName()).isEqualTo("Bench Press");
    }

    @Test
    void plan_pobieraniePoPlanId_zwracaPlan() throws Exception {
        String username = uniqueUser("plan_get");
        String token = register(username);

        MvcResult createResult = authPost("/plans", token,
                new TrainingPlanRequest("Mój plan", null, null, null, null), 201);
        Long planId = objectMapper.readValue(
                createResult.getResponse().getContentAsString(), TrainingPlanResponse.class).id();

        MvcResult getResult = authGet("/plans/" + planId, token, 200);
        TrainingPlanResponse fetched = objectMapper.readValue(
                getResult.getResponse().getContentAsString(), TrainingPlanResponse.class);

        assertThat(fetched.id()).isEqualTo(planId);
        assertThat(fetched.name()).isEqualTo("Mój plan");
    }

    @Test
    void plan_listaPlanow_zawieraStworzonyPlan() throws Exception {
        String username = uniqueUser("plan_list");
        String token = register(username);

        authPost("/plans", token,
                new TrainingPlanRequest("Plan A", null, null, null, null), 201);
        authPost("/plans", token,
                new TrainingPlanRequest("Plan B", null, null, null, null), 201);

        MvcResult result = authGet("/plans", token, 200);
        List<TrainingPlanResponse> plans = objectMapper.readValue(
                result.getResponse().getContentAsString(), new TypeReference<>() {});

        assertThat(plans).hasSizeGreaterThanOrEqualTo(2);
        assertThat(plans).extracting(TrainingPlanResponse::name)
                .contains("Plan A", "Plan B");
    }

    @Test
    void plan_aktualizacja_zmienionaNazwaIOpis() throws Exception {
        String username = uniqueUser("plan_upd");
        String token = register(username);

        MvcResult createResult = authPost("/plans", token,
                new TrainingPlanRequest("Stara nazwa", "Stary opis", null, null, null), 201);
        Long planId = objectMapper.readValue(
                createResult.getResponse().getContentAsString(), TrainingPlanResponse.class).id();

        MvcResult updateResult = authPut("/api/plans/" + planId, token,
                new TrainingPlanRequest("Nowa nazwa", "Nowy opis", null, null, null), 200);
        TrainingPlanResponse updated = objectMapper.readValue(
                updateResult.getResponse().getContentAsString(), TrainingPlanResponse.class);

        assertThat(updated.name()).isEqualTo("Nowa nazwa");
        assertThat(updated.description()).isEqualTo("Nowy opis");
    }

    @Test
    void plan_usuniecie_poUsuniecciuZwraca404() throws Exception {
        String username = uniqueUser("plan_del");
        String token = register(username);

        MvcResult createResult = authPost("/plans", token,
                new TrainingPlanRequest("Do usunięcia", null, null, null, null), 201);
        Long planId = objectMapper.readValue(
                createResult.getResponse().getContentAsString(), TrainingPlanResponse.class).id();

        // Usuń plan — 204 No Content
        authDelete("/api/plans/" + planId, token, 204);

        // Po usunięciu GET powinno zwrócić 404
        authGet("/plans/" + planId, token, 404);
    }

    @Test
    void plan_innyUzytkownik_nieMaDostepuDoCudzegoPlan() throws Exception {
        String userA = uniqueUser("owner_a");
        String userB = uniqueUser("viewer_b");
        String tokenA = register(userA);
        String tokenB = register(userB);

        MvcResult createResult = authPost("/plans", tokenA,
                new TrainingPlanRequest("Prywatny plan A", null, null, null, null), 201);
        Long planId = objectMapper.readValue(
                createResult.getResponse().getContentAsString(), TrainingPlanResponse.class).id();

        // Użytkownik B próbuje pobrać plan użytkownika A — 404
        authGet("/plans/" + planId, tokenB, 404);
    }

    // ==================== WALIDACJA PLANÓW ====================

    @Test
    void plan_walidacja_brakNazwy_zwraca400() throws Exception {
        String username = uniqueUser("plan_noname");
        String token = register(username);

        // name jest @NotBlank — pusta string nie przejdzie
        var request = new TrainingPlanRequest("", null, null, null, null);
        authPost("/plans", token, request, 400);
    }

    @Test
    void plan_walidacja_dataKoncaPrzedDataPoczatku_zwraca400() throws Exception {
        String username = uniqueUser("plan_dates");
        String token = register(username);

        var request = new TrainingPlanRequest(
                "Błędne daty",
                null,
                LocalDate.now().plusDays(10),  // startDate = za 10 dni
                LocalDate.now(),               // endDate = dzisiaj — PRZED startDate
                null
        );
        authPost("/plans", token, request, 400);
    }

    // ==================== ŚLEDZENIE WAGI ====================

    @Test
    void waga_dodajWpisISprawdzListe() throws Exception {
        String username = uniqueUser("weight_add");
        String token = register(username);

        var entry = new WeightEntryRequest(null, LocalDate.now(), new BigDecimal("75.5"));
        MvcResult result = authPost("/api/stats/weight", token, entry, 201);
        WeightProgressPointResponse saved = objectMapper.readValue(
                result.getResponse().getContentAsString(), WeightProgressPointResponse.class);

        assertThat(saved.weightKg()).isEqualByComparingTo(new BigDecimal("75.5"));
        assertThat(saved.date()).isEqualTo(LocalDate.now());

        // Sprawdź listę — powinien zawierać dzisiejszy wpis
        MvcResult listResult = authGet("/api/stats/weight", token, 200);
        List<WeightProgressPointResponse> entries = objectMapper.readValue(
                listResult.getResponse().getContentAsString(), new TypeReference<>() {});

        assertThat(entries).isNotEmpty();
        assertThat(entries).extracting(WeightProgressPointResponse::date)
                .contains(LocalDate.now());
    }

    @Test
    void waga_nadpisanieTegSamegoDnia_upsertNieDuplikat() throws Exception {
        String username = uniqueUser("weight_upsert");
        String token = register(username);
        LocalDate today = LocalDate.now();

        // Pierwsza waga
        authPost("/api/stats/weight", token,
                new WeightEntryRequest(null, today, new BigDecimal("80.0")), 201);

        // Druga waga tego samego dnia — powinna nadpisać
        authPost("/api/stats/weight", token,
                new WeightEntryRequest(null, today, new BigDecimal("79.5")), 201);

        MvcResult listResult = authGet("/api/stats/weight", token, 200);
        List<WeightProgressPointResponse> entries = objectMapper.readValue(
                listResult.getResponse().getContentAsString(), new TypeReference<>() {});

        long todayCount = entries.stream()
                .filter(e -> today.equals(e.date()))
                .count();
        assertThat(todayCount).isEqualTo(1);

        WeightProgressPointResponse todayEntry = entries.stream()
                .filter(e -> today.equals(e.date()))
                .findFirst().orElseThrow();
        assertThat(todayEntry.weightKg()).isEqualByComparingTo(new BigDecimal("79.5"));
    }

    @Test
    void waga_poczatkowoLista_pustaDlaNowegoUzytkownika() throws Exception {
        String username = uniqueUser("weight_empty");
        String token = register(username);

        MvcResult result = authGet("/api/stats/weight", token, 200);
        List<WeightProgressPointResponse> entries = objectMapper.readValue(
                result.getResponse().getContentAsString(), new TypeReference<>() {});

        assertThat(entries).isEmpty();
    }

    // ==================== KALENDARZ TRENINGÓW ====================

    @Test
    void kalendarz_uzytkownikBezTreningow_zwracaPustaListe() throws Exception {
        String username = uniqueUser("cal_empty");
        String token = register(username);

        MvcResult result = mockMvc.perform(get("/api/calendar")
                        .param("month", "5")
                        .param("year", "2025")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo("[]");
    }

    @Test
    void kalendarz_nieprawidlowyMiesiac0_zwraca400() throws Exception {
        String username = uniqueUser("cal_invalid");
        String token = register(username);

        mockMvc.perform(get("/api/calendar")
                        .param("month", "0")   // miesiąc 0 jest nieprawidłowy
                        .param("year", "2025")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());
    }

    @Test
    void kalendarz_nieprawidlowyMiesiac13_zwraca400() throws Exception {
        String username = uniqueUser("cal_month13");
        String token = register(username);

        mockMvc.perform(get("/api/calendar")
                        .param("month", "13")  // miesiąc 13 jest nieprawidłowy
                        .param("year", "2025")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());
    }

    // ==================== PROGRES SIŁOWY ====================

    @Test
    void progressSilowy_uzytkownikBezTreningow_zwracaPustaListe() throws Exception {
        String username = uniqueUser("strength_empty");
        String token = register(username);

        MvcResult result = mockMvc.perform(get("/api/stats/progress")
                        .param("exerciseId", "exercise_bench")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo("[]");
    }
}
