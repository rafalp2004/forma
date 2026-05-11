# API Contracts – Projekt Forma

Plik opisuje wszystkie zależności między modułami backendu.
Każda zmiana w interfejsie lub DTO wymaga poinformowania wszystkich osób które go używają i aktualizacji tego pliku.

**Ostatnia aktualizacja:** 2026-05-11  
**Zespół:** Arek (auth), Mateusz (workout), Antoni (planning), Rafał (social), Oskar (nutrition)
 
---

## Mapa zależności

```
Arek (User)        ←── używają wszyscy
Mateusz (Workout)  ←── używają: Antoni, Rafał
Antoni (Planning)  ←── nie udostępnia nic innym
Rafał (Social)     ←── nie udostępnia nic innym
Oskar (Nutrition)  ←── nie udostępnia nic innym
```
 
---

## 1. Moduł Auth (właściciel: Arek)

### Udostępnia wszystkim: `UserQueryService`

```java
package com.forma.shared.service;
 
public interface UserQueryService {
    UserDto findById(Long id);
    List<UserDto> searchByUsername(String query);
}
```

### DTO

```java
package com.forma.shared.dto;
 
public record UserDto(
    Long id,
    String username,
    String email
) {}
```

### Endpointy HTTP

| Metoda | Ścieżka | Opis | Wymaga auth |
|--------|---------|------|-------------|
| POST | `/api/auth/register` | Rejestracja nowego użytkownika | Nie |
| POST | `/api/auth/login` | Logowanie, zwraca JWT token | Nie |
| GET | `/api/users/{id}` | Dane użytkownika po ID | Tak |
| GET | `/api/users/search?query={q}` | Wyszukiwanie użytkowników po nazwie | Tak |
| GET | `/api/users/me` | Dane aktualnie zalogowanego użytkownika | Tak |
| PUT | `/api/users/me/biometrics` | Aktualizacja danych biometrycznych | Tak |
| PUT | `/api/users/me/goals` | Aktualizacja celów treningowych | Tak |

### Kto używa

| Moduł | Do czego |
|-------|----------|
| Mateusz | Powiązanie sesji treningowej z użytkownikiem |
| Antoni | Powiązanie planu treningowego z użytkownikiem |
| Rafał | Wyświetlanie profili znajomych, wyszukiwarka użytkowników |
| Oskar | Powiązanie dziennika posiłków z użytkownikiem |

### Adnotacja @CurrentUser

Arek dostarcza adnotację `@CurrentUser` której wszyscy używają w kontrolerach:

```java
@GetMapping("/api/friends")
public List<FriendDto> getFriends(@CurrentUser User user) {
    return socialService.getFriends(user.getId());
}
```
 
---

## 2. Moduł Workout (właściciel: Mateusz)

### Udostępnia: `WorkoutQueryService`

```java
package com.forma.shared.service;
 
public interface WorkoutQueryService {
 
    // Historia sesji treningowych użytkownika w podanym przedziale dat
    List<WorkoutSummaryDto> getHistory(Long userId, LocalDate from, LocalDate to);
 
    // Lista wszystkich ćwiczeń z bazy (cache z ExerciseDB)
    List<ExerciseDto> getAllExercises();
 
    // Szczegóły konkretnego ćwiczenia po jego stałym ID z ExerciseDB
    ExerciseDto getExerciseById(String exerciseId);
}
```

### DTO

```java
package com.forma.shared.dto;
 
// Podsumowanie jednej sesji treningowej
public record WorkoutSummaryDto(
    Long id,
    Long userId,
    LocalDateTime completedAt,
    Double totalVolumeKg,
    Integer totalSets,
    List<WorkoutSetDto> sets
) {}
 
// Pojedyncza seria w ramach sesji
public record WorkoutSetDto(
    String exerciseId,      // stałe ID z ExerciseDB
    String exerciseName,
    String muscleGroup,     // partia mięśniowa
    Integer reps,
    Double weightKg,
    LocalDateTime performedAt
) {}
 
// Ćwiczenie z bazy ExerciseDB
public record ExerciseDto(
    String id,              // stałe ID z ExerciseDB – nigdy się nie zmienia
    String name,
    String muscleGroup,
    String equipment,
    String gifUrl
) {}
```

### Endpointy HTTP

| Metoda | Ścieżka | Opis | Wymaga auth |
|--------|---------|------|-------------|
| GET | `/api/exercises` | Lista ćwiczeń z opcjonalnym filtrowaniem | Tak |
| GET | `/api/exercises?muscle={muscle}` | Ćwiczenia filtrowane po partii mięśniowej | Tak |
| GET | `/api/exercises/{id}` | Szczegóły ćwiczenia po ID | Tak |
| POST | `/api/workouts/sessions` | Utwórz nową sesję treningową | Tak |
| PUT | `/api/workouts/sessions/{id}` | Aktualizuj sesję (dodaj serie) | Tak |
| GET | `/api/workouts/sessions/{id}` | Szczegóły sesji | Tak |
| GET | `/api/workouts/history?userId={id}&from={date}&to={date}` | Historia treningów użytkownika | Tak |

### Kto używa

| Moduł | Do czego |
|-------|----------|
| Antoni | Lista ćwiczeń do tworzenia planów treningowych, historia treningów do wykresów progresji |
| Rafał | Historia treningów do obliczania score'u w wyzwaniach |

### Ważna uwaga

`exerciseId` to stałe ID z ExerciseDB API – nigdy się nie zmienia. Antoni i Rafał przechowują to ID u siebie i używają go do odwołań. Mateusz gwarantuje że to ID zawsze będzie poprawnie zmapowane.
 
---

## 3. Moduł Planning (właściciel: Antoni)

### Nie udostępnia żadnych interfejsów innym modułom.

### Zależności Antoniego

| Od kogo | Co pobiera | Interfejs |
|---------|-----------|-----------|
| Arek | Dane zalogowanego użytkownika | `UserQueryService.findById()` |
| Mateusz | Lista ćwiczeń do budowania planu | `WorkoutQueryService.getAllExercises()` |
| Mateusz | Szczegóły ćwiczenia po ID | `WorkoutQueryService.getExerciseById()` |
| Mateusz | Historia treningów do wykresów | `WorkoutQueryService.getHistory()` |

### Endpointy HTTP

| Metoda | Ścieżka | Opis | Wymaga auth |
|--------|---------|------|-------------|
| GET | `/api/plans?userId={id}` | Lista planów użytkownika | Tak |
| POST | `/api/plans` | Utwórz nowy plan | Tak |
| GET | `/api/plans/{id}` | Szczegóły planu | Tak |
| PUT | `/api/plans/{id}` | Edytuj plan | Tak |
| DELETE | `/api/plans/{id}` | Usuń plan | Tak |
| GET | `/api/stats/progress?userId={id}&exerciseId={id}` | Wykres progresji siły dla ćwiczenia | Tak |
| GET | `/api/stats/weight?userId={id}` | Wykres zmian wagi ciała | Tak |
| GET | `/api/calendar?userId={id}&month={month}&year={year}` | Kalendarz sesji treningowych | Tak |
 
---

## 4. Moduł Social (właściciel: Rafał)

### Nie udostępnia żadnych interfejsów innym modułom.

### Zależności Rafała

| Od kogo | Co pobiera | Interfejs |
|---------|-----------|-----------|
| Arek | Dane użytkownika do wyświetlania profilu znajomego | `UserQueryService.findById()` |
| Arek | Wyszukiwanie użytkowników po nazwie | `UserQueryService.searchByUsername()` |
| Mateusz | Historia treningów do obliczania score'u w wyzwaniach | `WorkoutQueryService.getHistory()` |

### Endpointy HTTP

| Metoda | Ścieżka | Opis | Wymaga auth |
|--------|---------|------|-------------|
| POST | `/api/friends/request` | Wyślij zaproszenie do znajomych | Tak |
| POST | `/api/friends/accept/{id}` | Akceptuj zaproszenie | Tak |
| POST | `/api/friends/reject/{id}` | Odrzuć zaproszenie | Tak |
| GET | `/api/friends?userId={id}` | Lista znajomych użytkownika | Tak |
| GET | `/api/friends/pending` | Lista oczekujących zaproszeń | Tak |
| GET | `/api/feed?userId={id}` | Feed aktywności znajomych | Tak |
| POST | `/api/challenges` | Utwórz wyzwanie | Tak |
| POST | `/api/challenges/{id}/join` | Dołącz do wyzwania | Tak |
| GET | `/api/challenges` | Lista aktywnych wyzwań | Tak |
| GET | `/api/challenges/{id}` | Szczegóły wyzwania | Tak |
| GET | `/api/challenges/{id}/leaderboard` | Ranking uczestników wyzwania | Tak |

### Jak obliczany jest score w wyzwaniach

Rafał pobiera historię treningów przez `WorkoutQueryService.getHistory(userId, startDate, endDate)` i na jej podstawie oblicza wynik uczestnika zgodnie z metryką wyzwania:

| Metryka | Sposób obliczania |
|---------|------------------|
| `TOTAL_VOLUME` | Suma `weightKg * reps` ze wszystkich serii w przedziale dat |
| `WORKOUT_COUNT` | Liczba ukończonych sesji w przedziale dat |
| `STREAK_DAYS` | Najdłuższa seria kolejnych dni z co najmniej jedną sesją |
 
---

## 5. Moduł Nutrition (właściciel: Oskar)

### Nie udostępnia żadnych interfejsów innym modułom.

### Zależności Oskara

| Od kogo | Co pobiera | Interfejs |
|---------|-----------|-----------|
| Arek | Dane zalogowanego użytkownika | `UserQueryService.findById()` |

### Endpointy HTTP

| Metoda | Ścieżka | Opis | Wymaga auth |
|--------|---------|------|-------------|
| GET | `/api/nutrition/products?query={q}` | Wyszukiwanie produktów spożywczych | Tak |
| GET | `/api/nutrition/products/{id}` | Szczegóły produktu | Tak |
| POST | `/api/nutrition/meals` | Dodaj wpis do dziennika posiłków | Tak |
| GET | `/api/nutrition/meals?userId={id}&date={date}` | Dziennik posiłków na dany dzień | Tak |
| DELETE | `/api/nutrition/meals/{id}` | Usuń wpis z dziennika | Tak |
| GET | `/api/nutrition/summary?userId={id}&date={date}` | Podsumowanie kalorii i makr na dany dzień | Tak |
 
---

## Zasady ogólne

1. Sygnatura metody interfejsu nie zmienia się bez rozmowy z każdą osobą która jej używa.
2. Nowe metody dodajemy do interfejsu – nie zmieniamy istniejących.
3. Zmiany w `shared/` commitujemy osobnym commitem z opisem co i dlaczego zmieniono.
4. DTO w pakiecie `com.forma.shared.dto`, interfejsy w `com.forma.shared.service`.
5. Jeśli potrzebujesz nowej metody od kolegi – zgłaszasz to przez PR, nie edytujesz jego kodu.
6. `exerciseId` z ExerciseDB jest stałym identyfikatorem – wszyscy go używają jako klucza obcego.
