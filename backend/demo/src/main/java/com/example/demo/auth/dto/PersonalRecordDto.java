package com.example.demo.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PersonalRecordDto {
    private String exerciseName;
    private Double weight;
    private LocalDateTime date;
}
