package com.fitness.userservice.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.fitness.userservice.Domain.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    boolean existsByEmail(String email);

    boolean existsByKeycloakId(String userId);

    User findByEmail(String email);
    
}
