package com.fitness.userservice.infrastructure.dto;

import java.time.LocalDateTime;

import com.fitness.userservice.domain.enums.UserRole;

import lombok.Data;

@Data
public class UserResponse {
    private String id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String keycloakId;
    private UserRole role = UserRole.USER;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
