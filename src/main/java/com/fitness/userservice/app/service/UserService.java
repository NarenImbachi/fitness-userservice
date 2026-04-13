package com.fitness.userservice.app.service;

import com.fitness.userservice.app.ports.in.IUserServicePort;
import com.fitness.userservice.app.ports.out.IUserPersistencePort;
import com.fitness.userservice.domain.model.User;
import com.fitness.userservice.infrastructure.dto.RegisterRequest;
import com.fitness.userservice.infrastructure.dto.UpdateUserRequest;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements IUserServicePort {

    private final IUserPersistencePort userPersistencePort;
    private final Keycloak keycloak;

    @Value("${keycloak.admin.realm}")
    private String realm;

    @Override
    public User getUserProfile(String userId) {
        return userPersistencePort.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public User registerUser(RegisterRequest request) {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(request.getEmail());
        userRepresentation.setEmail(request.getEmail());
        userRepresentation.setFirstName(request.getFirstName());
        userRepresentation.setLastName(request.getLastName());
        userRepresentation.setEnabled(true);
        userRepresentation.setEmailVerified(true);

        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setValue(request.getPassword());
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);

        userRepresentation.setCredentials(Collections.singletonList(credentialRepresentation));

        UsersResource usersResource = keycloak.realm(realm).users();
        Response response = usersResource.create(userRepresentation);

        if (response.getStatus() == 201) {
            String location = response.getLocation().getPath();
            String keycloakId = location.substring(location.lastIndexOf('/') + 1);

            User user = new User();
            user.setEmail(request.getEmail());
            user.setPassword(request.getPassword()); // Consider hashing or not storing raw password
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setKeycloakId(keycloakId);
            return userPersistencePort.save(user);
        } else {
            log.error("Could not create user in Keycloak. Status: {}, Body: {}", response.getStatus(), response.readEntity(String.class));
            throw new RuntimeException("Could not create user in Keycloak. Status: " + response.getStatus());
        }
    }

    @Override
    public Boolean existByUserId(String userId) {
        log.info("Calling user validation API for userId: {}", userId);
        return userPersistencePort.existsByKeycloakId(userId);
    }

    @Override
    public User updateUser(String userId, UpdateUserRequest request) {
        User user = userPersistencePort.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found") );

        // Actualizar solo los campos que no son nulos en la petición
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getBirthDate() != null) {
            user.setBirthDate(request.getBirthDate());
        }
        if (request.getHeight() != null) {
            user.setHeight(request.getHeight());
        }
        if (request.getWeight() != null) {
            user.setWeight(request.getWeight());
        }

        return userPersistencePort.save(user);
    }
}

