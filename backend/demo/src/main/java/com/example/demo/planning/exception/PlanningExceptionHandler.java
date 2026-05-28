package com.example.demo.planning.exception;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.example.demo.planning")
@Order(1)  // wyższy priorytet niż GlobalExceptionHandler (domyślny LOWEST_PRECEDENCE)
public class PlanningExceptionHandler {

    @ExceptionHandler(TrainingPlanNotFoundException.class)
    public ResponseEntity<String> handleTrainingPlanNotFound(TrainingPlanNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
