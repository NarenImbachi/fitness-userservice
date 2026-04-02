package com.fitness.userservice.service;

import org.springframework.stereotype.Service;

import com.fitness.userservice.Domain.model.User;
import com.fitness.userservice.Repository.UserRepository;
import com.fitness.userservice.dto.RegisterRequest;
import com.fitness.userservice.dto.UserResponse;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {

    private UserRepository userRepository;

    public UserResponse getUserProfile(String userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setPassword(user.getPassword());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }
    
    public UserResponse registerUser(RegisterRequest request) {

        // Validamos si el usuario ya existe por email, si existe retornamos su información, si no existe lo creamos
        if(userRepository.existsByEmail(request.getEmail())) {
            User existingUser = userRepository.findByEmail(request.getEmail());
            UserResponse response = new UserResponse();
            response.setId(existingUser.getId());
            response.setEmail(existingUser.getEmail());
            response.setFirstName(existingUser.getFirstName());
            response.setLastName(existingUser.getLastName());
            response.setCreatedAt(existingUser.getCreatedAt());
            response.setUpdatedAt(existingUser.getUpdatedAt());
            response.setKeycloakId(existingUser.getKeycloakId());
            return response;
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setKeycloakId(request.getKeycloakId());
        User savedUser = userRepository.save(user);

        UserResponse response = new UserResponse();
        response.setId(savedUser.getId());
        response.setEmail(savedUser.getEmail());
        response.setFirstName(savedUser.getFirstName());
        response.setLastName(savedUser.getLastName());
        response.setCreatedAt(savedUser.getCreatedAt());
        response.setUpdatedAt(savedUser.getUpdatedAt());
        response.setKeycloakId(savedUser.getKeycloakId());
        return response;
    }

    public Boolean existByUserId(String userId) {
        log.info("Calling user validation API for userId: {}", userId);
        return userRepository.existsByKeycloakId(userId);
    }
}
