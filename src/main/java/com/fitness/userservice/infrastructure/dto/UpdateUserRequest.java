package com.fitness.userservice.infrastructure.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class UpdateUserRequest {
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private Double height;
    private Double weight;
}
