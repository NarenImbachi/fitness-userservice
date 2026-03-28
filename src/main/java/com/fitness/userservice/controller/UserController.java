package com.fitness.userservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fitness.userservice.dto.RegisterRequest;
import com.fitness.userservice.dto.UserResponse;
import com.fitness.userservice.service.UserService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {

    private UserService userService;
    
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserProfile(@PathVariable String userId) {   
        return ResponseEntity.ok(userService.getUserProfile(userId));
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody RegisterRequest request) {   
        return ResponseEntity.ok(userService.registerUser(request));
    }

    /**
     * Endpoint para validar si un usuario existe por su userId. Retorna true si el usuario existe, false en caso contrario.
     * Este endpoint es útil para otros servicios que necesitan verificar la existencia de un usuario sin necesidad de obtener toda su información.
     * @param userId El ID del usuario a validar.
     * @return ResponseEntity<Boolean> indicando si el usuario existe o no.
     */
    @GetMapping("/{userId}/validate")
    public ResponseEntity<Boolean> validateUser(@PathVariable String userId) {   
        return ResponseEntity.ok(userService.existByUserId(userId));
    }
}
