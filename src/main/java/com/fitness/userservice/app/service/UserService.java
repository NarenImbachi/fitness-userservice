package com.fitness.userservice.app.service;

import org.springframework.stereotype.Service;

import com.fitness.userservice.app.ports.in.IUserServicePort;
import com.fitness.userservice.app.ports.out.IUserPersistencePort;
import com.fitness.userservice.domain.model.User;
import com.fitness.userservice.infrastructure.dto.RegisterRequest;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class UserService implements IUserServicePort {

    private final IUserPersistencePort userPersistencePort;

    @Override
    public User getUserProfile(String userId) {
        return userPersistencePort.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        //UserResponse response = new UserResponse();
        //response.setId(user.getId());
        //response.setEmail(user.getEmail());
        //response.setPassword(user.getPassword());
        //response.setFirstName(user.getFirstName());
        //response.setLastName(user.getLastName());
        //response.setCreatedAt(user.getCreatedAt());
        //response.setUpdatedAt(user.getUpdatedAt());
        //return response;
    }
    
    @Override
    public User registerUser(RegisterRequest request) {

        // Validamos si el usuario ya existe por email, si existe retornamos su información, si no existe lo creamos
        if(userPersistencePort.existsByEmail(request.getEmail())) {
            User existingUser = userPersistencePort.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

            User userExist = new User();
            userExist.setId(existingUser.getId());
            userExist.setEmail(existingUser.getEmail());
            userExist.setFirstName(existingUser.getFirstName());
            userExist.setLastName(existingUser.getLastName());
            userExist.setCreatedAt(existingUser.getCreatedAt());
            userExist.setUpdatedAt(existingUser.getUpdatedAt());
            userExist.setKeycloakId(existingUser.getKeycloakId());
            return userExist;
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setKeycloakId(request.getKeycloakId());
        User savedUser = userPersistencePort.save(user);

        //UserResponse response = new UserResponse();
        //response.setId(savedUser.getId());
        //response.setEmail(savedUser.getEmail());
        //response.setFirstName(savedUser.getFirstName());
        //response.setLastName(savedUser.getLastName());
        //response.setCreatedAt(savedUser.getCreatedAt());
        //response.setUpdatedAt(savedUser.getUpdatedAt());
        //response.setKeycloakId(savedUser.getKeycloakId());
        return savedUser;
    }

    @Override
    public Boolean existByUserId(String userId) {
        log.info("Calling user validation API for userId: {}", userId);
        return userPersistencePort.existsByKeycloakId(userId);
    }
}
