package com.example.demo.planning.exception;

public class TrainingPlanNotFoundException extends RuntimeException {
    public TrainingPlanNotFoundException(Long id) {
        super("Training plan with id " + id + " not found");
    }
}
