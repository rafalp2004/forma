package com.example.demo.planning.service;

import com.example.demo.auth.entity.User;
import com.example.demo.auth.repository.UserRepository;
import com.example.demo.planning.dto.CalendarWorkoutResponse;
import com.example.demo.planning.dto.PlanExerciseRequest;
import com.example.demo.planning.dto.StrengthProgressPointResponse;
import com.example.demo.planning.dto.TrainingPlanRequest;
import com.example.demo.planning.dto.TrainingPlanResponse;
import com.example.demo.planning.dto.WeightEntryRequest;
import com.example.demo.planning.dto.WeightProgressPointResponse;
import com.example.demo.planning.entity.PlanExercise;
import com.example.demo.planning.entity.TrainingPlan;
import com.example.demo.planning.entity.WeightEntry;
import com.example.demo.planning.exception.TrainingPlanNotFoundException;
import com.example.demo.planning.mapper.PlanExerciseMapper;
import com.example.demo.planning.mapper.TrainingPlanMapper;
import com.example.demo.planning.mapper.WeightEntryMapper;
import com.example.demo.planning.repository.TrainingPlanRepository;
import com.example.demo.planning.repository.WeightEntryRepository;
import com.example.demo.shared.dto.WorkoutSetDto;
import com.example.demo.shared.dto.WorkoutSummaryDto;
import com.example.demo.shared.services.UserQueryService;
import com.example.demo.shared.services.WorkoutQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PlanningServiceImpl implements PlanningService {

    private static final String DEFAULT_WORKOUT_NAME = "Workout";

    private final TrainingPlanRepository trainingPlanRepository;
    private final WeightEntryRepository weightEntryRepository;
    private final TrainingPlanMapper trainingPlanMapper;
    private final PlanExerciseMapper planExerciseMapper;
    private final WeightEntryMapper weightEntryMapper;
    private final WorkoutQueryService workoutQueryService;
    private final UserQueryService userQueryService;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TrainingPlanResponse> getPlans(Long userId) {
        validateUserExists(userId);
        return trainingPlanRepository.findByUserId(userId)
                .stream()
                .map(trainingPlanMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public TrainingPlanResponse createPlan(Long userId, TrainingPlanRequest request) {
        validateUserExists(userId);
        validateDateRange(request);

        TrainingPlan plan = trainingPlanMapper.toEntity(request, userId);
        replaceExercises(plan, request.exercises());

        return trainingPlanMapper.toResponse(trainingPlanRepository.save(plan));
    }

    @Override
    @Transactional(readOnly = true)
    public TrainingPlanResponse getPlan(Long userId, Long id) {
        validateUserExists(userId);
        return trainingPlanRepository.findByIdAndUserId(id, userId)
                .map(trainingPlanMapper::toResponse)
                .orElseThrow(() -> new TrainingPlanNotFoundException(id));
    }

    @Override
    @Transactional
    public TrainingPlanResponse updatePlan(Long userId, Long id, TrainingPlanRequest request) {
        validateUserExists(userId);
        validateDateRange(request);

        TrainingPlan plan = trainingPlanRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new TrainingPlanNotFoundException(id));

        trainingPlanMapper.updateEntity(plan, request);
        replaceExercises(plan, request.exercises());

        return trainingPlanMapper.toResponse(trainingPlanRepository.save(plan));
    }

    @Override
    @Transactional
    public void deletePlan(Long userId, Long id) {
        validateUserExists(userId);
        TrainingPlan plan = trainingPlanRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new TrainingPlanNotFoundException(id));
        trainingPlanRepository.delete(plan);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StrengthProgressPointResponse> getStrengthProgress(Long userId, String exerciseId) {
        validateUserExists(userId);
        if (exerciseId == null || exerciseId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Exercise id is required");
        }

        LocalDate to = LocalDate.now();
        LocalDate from = to.minusMonths(6);

        return getWorkoutHistory(userId, from, to)
                .stream()
                .filter(workout -> workout.sets() != null)
                .flatMap(workout -> workout.sets().stream())
                .filter(set -> Objects.equals(set.exerciseId(), exerciseId))
                .filter(set -> set.performedAt() != null && set.weightKg() != null)
                .collect(java.util.stream.Collectors.groupingBy(
                        set -> set.performedAt().toLocalDate(),
                        java.util.stream.Collectors.maxBy(Comparator.comparing(WorkoutSetDto::weightKg))
                ))
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().isPresent())
                .map(entry -> {
                    WorkoutSetDto set = entry.getValue().get();
                    return new StrengthProgressPointResponse(
                            entry.getKey(),
                            set.exerciseName(),
                            BigDecimal.valueOf(set.weightKg())
                    );
                })
                .sorted(Comparator.comparing(StrengthProgressPointResponse::date))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WeightProgressPointResponse> getWeightProgress(Long userId) {
        validateUserExists(userId);
        return weightEntryRepository.findByUserIdOrderByDateAsc(userId)
                .stream()
                .map(weightEntryMapper::toProgressResponse)
                .toList();
    }

    @Override
    @Transactional
    public WeightProgressPointResponse saveWeightEntry(Long userId, WeightEntryRequest request) {
        validateUserExists(userId);

        WeightEntry entry = weightEntryRepository.findByUserIdAndDate(userId, request.date())
                .orElseGet(() -> WeightEntry.builder()
                        .userId(userId)
                        .date(request.date())
                        .build());

        entry.setWeightKg(request.weightKg());

        WeightEntry saved = weightEntryRepository.save(entry);
        updateProfileWeightFromLatestEntry(userId);

        return weightEntryMapper.toProgressResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CalendarWorkoutResponse> getCalendar(Long userId, Integer month, Integer year) {
        validateUserExists(userId);
        if (month == null || month < 1 || month > 12) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Month must be between 1 and 12");
        }
        if (year == null || year < 1900) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Year is invalid");
        }

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate from = yearMonth.atDay(1);
        LocalDate to = yearMonth.atEndOfMonth();

        return getWorkoutHistory(userId, from, to)
                .stream()
                .filter(workout -> workout.completedAt() != null)
                .map(this::toCalendarWorkout)
                .sorted(Comparator.comparing(CalendarWorkoutResponse::date))
                .toList();
    }

    private void replaceExercises(TrainingPlan plan, List<PlanExerciseRequest> exerciseRequests) {
        if (plan.getExercises() == null) {
            plan.setExercises(new ArrayList<>());
        }

        plan.getExercises().clear();
        if (exerciseRequests == null) {
            return;
        }

        for (PlanExerciseRequest exerciseRequest : exerciseRequests) {
            PlanExercise exercise = planExerciseMapper.toEntity(exerciseRequest, plan);
            plan.getExercises().add(exercise);
        }
    }

    private CalendarWorkoutResponse toCalendarWorkout(WorkoutSummaryDto workout) {
        String workoutName = workout.sets() == null || workout.sets().isEmpty()
                ? DEFAULT_WORKOUT_NAME
                : workout.sets().get(0).exerciseName();

        return new CalendarWorkoutResponse(
                workout.completedAt().toLocalDate(),
                workout.id(),
                workoutName
        );
    }

    private void validateDateRange(TrainingPlanRequest request) {
        if (request.startDate() != null
                && request.endDate() != null
                && request.endDate().isBefore(request.startDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End date cannot be before start date");
        }
    }

    private void validateUserExists(Long userId) {
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User id is required");
        }
        userQueryService.findById(userId);
    }

    private void updateProfileWeightFromLatestEntry(Long userId) {
        BigDecimal latestWeight = weightEntryRepository.findByUserIdOrderByDateAsc(userId)
                .stream()
                .reduce((previous, current) -> current)
                .map(WeightEntry::getWeightKg)
                .orElse(null);
        if (latestWeight == null) {
            return;
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        user.setWeight(latestWeight.doubleValue());
        userRepository.save(user);
    }

    private List<WorkoutSummaryDto> getWorkoutHistory(Long userId, LocalDate from, LocalDate to) {
        List<WorkoutSummaryDto> history = workoutQueryService.getHistory(userId, from, to);
        return history == null ? List.of() : history;
    }
}
