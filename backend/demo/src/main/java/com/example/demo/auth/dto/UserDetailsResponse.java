package com.example.demo.auth.dto;

import com.example.demo.auth.entity.Gender;
import com.example.demo.auth.entity.Role;
import com.example.demo.auth.entity.UserGoal;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsResponse {
    private String username;
    private String email;
    private Integer age;
    private Double weight;
    private Double height;
    private Gender gender;
    private UserGoal goal;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
