package com.fitness.userservice.app.ports.in;

import com.fitness.userservice.domain.model.User;
import com.fitness.userservice.infrastructure.dto.RegisterRequest;
import com.fitness.userservice.infrastructure.dto.UpdateUserRequest;

/**
 * Puerto de entrada que define los casos de uso para la gestión de usuarios.
 * Los controladores (adaptadores de entrada) usarán este puerto para invocar la lógica de negocio.
 */
public interface IUserServicePort {

    User getUserProfile(String userId);

    Boolean existByUserId(String userId);

    User registerUser(RegisterRequest request);

    User updateUser(String userId, UpdateUserRequest request);
    
}
