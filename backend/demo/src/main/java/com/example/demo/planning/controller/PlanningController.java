package com.example.demo.planning.controller;

import com.example.demo.auth.entity.User;
import com.example.demo.auth.security.CurrentUser;
import com.example.demo.planning.dto.CalendarWorkoutResponse;
import com.example.demo.planning.dto.StrengthProgressPointResponse;
import com.example.demo.planning.dto.TrainingPlanRequest;
import com.example.demo.planning.dto.TrainingPlanResponse;
import com.example.demo.planning.dto.WeightEntryRequest;
import com.example.demo.planning.dto.WeightProgressPointResponse;
import com.example.demo.planning.service.PlanningService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PlanningController {

    private final PlanningService planningService;

    @GetMapping("/api/plans")
    public List<TrainingPlanResponse> getPlans(@RequestParam Long userId) {
        return planningService.getPlans(userId);
    }

    @PostMapping("/api/plans")
    @ResponseStatus(HttpStatus.CREATED)
    public TrainingPlanResponse createPlan(@CurrentUser User currentUser,
                                           @Valid @RequestBody TrainingPlanRequest request) {
        return planningService.createPlan(resolveCurrentUserId(currentUser), request);
    }

    @GetMapping("/api/plans/{id}")
    public TrainingPlanResponse getPlan(@PathVariable Long id) {
        return planningService.getPlan(id);
    }

    @PutMapping("/api/plans/{id}")
    public TrainingPlanResponse updatePlan(@PathVariable Long id,
                                           @Valid @RequestBody TrainingPlanRequest request) {
        return planningService.updatePlan(id, request);
    }

    @DeleteMapping("/api/plans/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePlan(@PathVariable Long id) {
        planningService.deletePlan(id);
    }

    @GetMapping("/api/stats/progress")
    public List<StrengthProgressPointResponse> getStrengthProgress(@RequestParam Long userId,
                                                                   @RequestParam String exerciseId) {
        return planningService.getStrengthProgress(userId, exerciseId);
    }

    @GetMapping("/api/stats/weight")
    public List<WeightProgressPointResponse> getWeightProgress(@RequestParam Long userId) {
        return planningService.getWeightProgress(userId);
    }

    @PostMapping("/api/stats/weight")
    @ResponseStatus(HttpStatus.CREATED)
    public WeightProgressPointResponse saveWeightEntry(@Valid @RequestBody WeightEntryRequest request) {
        return planningService.saveWeightEntry(request);
    }

    @GetMapping("/api/calendar")
    public List<CalendarWorkoutResponse> getCalendar(@RequestParam Long userId,
                                                     @RequestParam Integer month,
                                                     @RequestParam Integer year) {
        return planningService.getCalendar(userId, month, year);
    }

    private Long resolveCurrentUserId(User currentUser) {
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user is required");
        }
        return currentUser.getId();
    }
}
