package com.fitness.userservice.infrastructure.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fitness.userservice.domain.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    boolean existsByEmail(String email);

    boolean existsByKeycloakId(String userId);

    Optional<User> findByEmail(String email);
    
    void deleteById(String id);

    Optional<User> findByKeycloakId(String userId);
}
