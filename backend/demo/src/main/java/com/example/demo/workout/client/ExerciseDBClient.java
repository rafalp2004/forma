package com.example.demo.workout.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExerciseDBClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String BASE_URL = "https://oss.exercisedb.dev/api/v1/exercises";

    public List<ExternalExerciseDto> fetchExercises() {
        List<ExternalExerciseDto> allExercises = new ArrayList<>();
        String cursor = null;
        boolean hasNext = true;
        int pageCounter = 1;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");

        while (hasNext) {
            String url = (cursor == null)
                    ? BASE_URL + "?limit=25"
                    : BASE_URL + "?limit=25&after=" + cursor;

            log.info("Pobieranie paczki danych nr {} z URL: {}", pageCounter, url);

            try {
                ResponseEntity<ApiResponseDto> response = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        new HttpEntity<>(headers),
                        ApiResponseDto.class
                );

                ApiResponseDto body = response.getBody();
                if (body != null && body.data() != null && !body.data().isEmpty()) {
                    allExercises.addAll(body.data());
                    log.info("Dodano {} ćwiczeń. Łącznie pobrano: {}", body.data().size(), allExercises.size());

                    hasNext = body.meta().hasNextPage();
                    cursor = body.meta().nextCursor();
                    pageCounter++;
                } else {
                    log.warn("Serwer zwrócił pustą paczkę danych.");
                    hasNext = false;
                }
            } catch (Exception e) {
                log.error("Błąd podczas pobierania paczki nr {}: {}", pageCounter, e.getMessage());
                break;
            }
        }

        log.info("Zakończono pobieranie z API. Całkowita liczba zebranych ćwiczeń: {}", allExercises.size());
        return allExercises;
    }

    public record ApiResponseDto(
            boolean success,
            MetaDto meta,
            List<ExternalExerciseDto> data
    ) {}

    public record MetaDto(
            boolean hasNextPage,
            String nextCursor
    ) {}

    public record ExternalExerciseDto(
            String exerciseId,
            String name,
            List<String> targetMuscles,
            List<String> equipments,
            String gifUrl,
            List<String> instructions
    ) {}
}