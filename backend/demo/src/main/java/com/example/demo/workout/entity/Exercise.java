package com.example.demo.workout.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "exercises")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Exercise {

    @Id
    private String id;
    private String name;
    private String muscleGroup;
    private String equipment;
    private String gifUrl;

    @Column(columnDefinition = "TEXT")
    private String instructions;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Exercise exercise)) return false;
        return id != null && id.equals(exercise.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}