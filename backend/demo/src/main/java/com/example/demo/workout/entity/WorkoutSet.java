package com.example.demo.workout.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "workout_sets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private WorkoutSession session;

    private String exerciseId;
    private Integer reps;
    private Double weight;
    private LocalDateTime performedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WorkoutSet workoutSet)) return false;
        return id != null && id.equals(workoutSet.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}