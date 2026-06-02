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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PlanningController {

    private final PlanningService planningService;

    @GetMapping("/plans")
    public List<TrainingPlanResponse> getPlans(@CurrentUser User currentUser) {
        return planningService.getPlans(resolveCurrentUserId(currentUser));
    }

    @PostMapping("/plans")
    @ResponseStatus(HttpStatus.CREATED)
    public TrainingPlanResponse createPlan(@CurrentUser User currentUser,
                                           @Valid @RequestBody TrainingPlanRequest request) {
        return planningService.createPlan(resolveCurrentUserId(currentUser), request);
    }

    @GetMapping("/plans/{id}")
    public TrainingPlanResponse getPlan(@CurrentUser User currentUser,
                                        @PathVariable Long id) {
        return planningService.getPlan(resolveCurrentUserId(currentUser), id);
    }

    @PutMapping("/plans/{id}")
    public TrainingPlanResponse updatePlan(@CurrentUser User currentUser,
                                           @PathVariable Long id,
                                           @Valid @RequestBody TrainingPlanRequest request) {
        return planningService.updatePlan(resolveCurrentUserId(currentUser), id, request);
    }

    @DeleteMapping("/plans/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePlan(@CurrentUser User currentUser,
                           @PathVariable Long id) {
        planningService.deletePlan(resolveCurrentUserId(currentUser), id);
    }

    @GetMapping("/stats/progress")
    public List<StrengthProgressPointResponse> getStrengthProgress(@CurrentUser User currentUser,
                                                                   @RequestParam String exerciseId) {
        return planningService.getStrengthProgress(resolveCurrentUserId(currentUser), exerciseId);
    }

    @GetMapping("/stats/weight")
    public List<WeightProgressPointResponse> getWeightProgress(@CurrentUser User currentUser) {
        return planningService.getWeightProgress(resolveCurrentUserId(currentUser));
    }

    @PostMapping("/stats/weight")
    @ResponseStatus(HttpStatus.CREATED)
    public WeightProgressPointResponse saveWeightEntry(@CurrentUser User currentUser,
                                                       @Valid @RequestBody WeightEntryRequest request) {
        return planningService.saveWeightEntry(resolveCurrentUserId(currentUser), request);
    }

    @GetMapping("/calendar")
    public List<CalendarWorkoutResponse> getCalendar(@CurrentUser User currentUser,
                                                     @RequestParam Integer month,
                                                     @RequestParam Integer year) {
        return planningService.getCalendar(resolveCurrentUserId(currentUser), month, year);
    }

    private Long resolveCurrentUserId(User currentUser) {
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user is required");
        }
        return currentUser.getId();
    }
}
