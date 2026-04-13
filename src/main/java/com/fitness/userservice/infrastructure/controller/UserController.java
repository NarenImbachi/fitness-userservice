package com.fitness.userservice.infrastructure.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fitness.userservice.app.ports.in.IUserServicePort;
import com.fitness.userservice.domain.model.User;
import com.fitness.userservice.infrastructure.dto.RegisterRequest;
import com.fitness.userservice.infrastructure.dto.UpdateUserRequest;
import com.fitness.userservice.infrastructure.dto.UserResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserServicePort userServicePort;
    
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserProfile(@PathVariable String userId) {   
        User user = userServicePort.getUserProfile(userId);
        
        UserResponse response = mapToUserResponse(user);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getCurrentUserProfile(@RequestHeader("X-User-ID") String userId) {
        User user = userServicePort.getUserProfile(userId);
        UserResponse response = mapToUserResponse(user);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody RegisterRequest request) {   
        User user = userServicePort.registerUser(request);
        UserResponse response = mapToUserResponse(user);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para validar si un usuario existe por su userId. Retorna true si el usuario existe, false en caso contrario.
     * Este endpoint es útil para otros servicios que necesitan verificar la existencia de un usuario sin necesidad de obtener toda su información.
     * @param userId El ID del usuario a validar.
     * @return ResponseEntity<Boolean> indicando si el usuario existe o no.
     */
    @GetMapping("/{userId}/validate")
    public ResponseEntity<Boolean> validateUser(@PathVariable String userId) {   
        return ResponseEntity.ok(userServicePort.existByUserId(userId));
    }

    /**
     * Endpoint para actualizar el perfil del usuario autenticado. El userId se obtiene del header "X-User-ID" que debe ser incluido en la solicitud.
     * @param userId El ID del usuario autenticado, obtenido del header "X-User-ID".
     * @param request El objeto UpdateUserRequest que contiene los campos a actualizar en el perfil del usuario.
     * @return ResponseEntity<UserResponse> con la información actualizada del usuario.
     */
    @PutMapping("/profile")
    public ResponseEntity<UserResponse> updateUserProfile(@RequestHeader("X-User-ID") String userId, @RequestBody UpdateUserRequest request) {
        User updatedUser = userServicePort.updateUser(userId, request);
        UserResponse response = mapToUserResponse(updatedUser);
        return ResponseEntity.ok(response);
    }

    /**
     * Método privado de utilidad para mapear un objeto de dominio User a un DTO UserResponse.
     * Esta responsabilidad pertenece al controlador (la capa de adaptación).
     * @param user El objeto de dominio User.
     * @return El DTO UserResponse.
     */
    private UserResponse mapToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        response.setKeycloakId(user.getKeycloakId());
        response.setBirthDate(user.getBirthDate());
        response.setHeight(user.getHeight());
        response.setWeight(user.getWeight());
        return response;
    }
}
