package com.fitness.userservice.app.ports.out;

import com.fitness.userservice.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface IUserPersistencePort {
    /**
     * Guarda un nuevo usuario o actualiza uno existente.
     *
     * @param user El usuario a guardar.
     * @return El usuario guardado (usualmente con el ID asignado).
     */
    User save(User user);

    /**
     * Busca un usuario por su ID.
     *
     * @param userId El ID del usuario.
     * @return Un Optional conteniendo el usuario si se encuentra, o vacío si no.
     */
    Optional<User> findById(String userId);

    /**
     * Busca un usuario por su dirección de email.
     *
     * @param email El email del usuario.
     * @return Un Optional conteniendo el usuario si se encuentra, o vacío si no.
     */
    Optional<User> findByEmail(String email);

    /**
     * Verifica si un usuario existe por su dirección de email.
     *
     * @param email El email del usuario.
     * @return true si el usuario existe, false en caso contrario.
     */
    boolean existsByEmail(String email);

    /**
     * Obtiene todos los usuarios.
     *
     * @return Una lista de todos los usuarios.
     */
    List<User> getAll();

    /**
     * Elimina un usuario por su ID.
     *
     * @param userId El ID del usuario a eliminar.
     */
    void deleteById(String userId);

    /**
     * Verifica si un usuario existe por su ID de Keycloak.
     * @param userId El ID de Keycloak del usuario.
     * @return  true si el usuario existe, false en caso contrario.
     */
    boolean existsByKeycloakId(String userId);
}
