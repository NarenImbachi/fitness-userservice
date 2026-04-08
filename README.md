# User Service - Fitness Application

## 1. Descripción General

El **User Service** es un microservicio fundamental dentro de la plataforma Fitness. Su principal responsabilidad es gestionar toda la información relacionada con los usuarios, incluyendo el registro, la autenticación (a través de Keycloak ID), la gestión de perfiles y la validación de existencia.

Este servicio está diseñado para ser el único punto de verdad para los datos de los usuarios, proporcionando una API RESTful para que otros servicios del ecosistema puedan interactuar con él de forma segura y eficiente.

## 2. Arquitectura

Este microservicio ha sido refactorizado para implementar una **Arquitectura Hexagonal (Puertos y Adaptadores)**. Esta decisión arquitectónica tiene como objetivo principal desacoplar la lógica de negocio del dominio de las tecnologías externas (frameworks, bases de datos, etc.).

La estructura del proyecto se organiza de la siguiente manera:

-   **`domain`**: Contiene los modelos de negocio puros (ej. `User`). No tiene dependencias de ningún framework. Es el corazón de la aplicación.
-   **`application`**: Contiene la lógica de orquestación de los casos de uso.
    -   **`ports/in`**: Define las interfaces para los casos de uso (ej. `IUserServicePort`). Son los "puertos de entrada".
    -   **`ports/out`**: Define las interfaces para las dependencias externas que la aplicación necesita (ej. `IUserPersistencePort`). Son los "puertos de salida".
    -   **`service`**: Implementa los puertos de entrada, orquestando la lógica y utilizando los puertos de salida.
-   **`infrastructure`**: Contiene las implementaciones concretas de los puertos y toda la tecnología externa.
    -   **`controllers`**: Adaptadores de entrada que exponen la API REST. Dependen de los puertos de entrada.
    -   **`adapters`**: Adaptadores de salida que implementan los puertos de salida (ej. `UserPersistenceAdapter` para JPA).
    -   **`repository`**: Interfaces de Spring Data JPA.
    -   **`dto`**: Objetos de Transferencia de Datos para las peticiones y respuestas de la API.

Este enfoque garantiza que el dominio permanezca aislado, facilitando las pruebas unitarias, la mantenibilidad y la flexibilidad para cambiar tecnologías en el futuro sin impactar la lógica de negocio.

## 3. Tecnologías Utilizadas

-   **Java**: `21`
-   **Spring Boot**: `3.5.13`
-   **Spring Cloud**: `2025.0.1`
-   **Base de Datos**: PostgreSQL
-   **Persistencia**: Spring Data JPA / Hibernate
-   **Service Discovery**: Netflix Eureka
-   **Configuración Centralizada**: Spring Cloud Config
-   **Build Tool**: Maven
-   **Librerías**: Lombok, Spring Web, Spring Boot Actuator.

## 4. Configuración y Ejecución

### Prerrequisitos

-   Java 21 o superior.
-   Maven 3.8 o superior.
-   Una instancia de PostgreSQL en ejecución.
-   Tener los servicios **Eureka Server** y **Config Server** en ejecución.

### Configuración

1.  **Configuración Centralizada**: El servicio obtiene su configuración del **Config Server**. Asegúrate de que el Config Server esté apuntando al repositorio correcto y que el archivo `userservice.yml` contenga las siguientes propiedades (ajusta los valores según tu entorno):

    ```yaml
    spring:
      datasource:
        url: ${DB_URL:jdbc:postgresql://localhost:5432/fitness_user_db}
        username: ${DB_USER:postgres}
        password: ${DB_PASSWORD:root}
        driver-class-name: org.postgresql.Driver
      jpa:
        hibernate:
          ddl-auto: create-drop # O 'update' para desarrollo persistente
        properties:
          hibernate:
            dialect: org.hibernate.dialect.PostgreSQLDialect
    
    eureka:
      client:
        serviceUrl:
          defaultZone: ${EUREKA_SERVER_URL:http://localhost:8761/eureka/}
    
    server:
      port: ${PORT:8081}
    ```

2.  **Configuración Local**: El archivo `src/main/resources/application.yaml` solo necesita especificar el nombre de la aplicación y la ubicación del Config Server.

    ```yaml
    spring:
      application:
        name: userservice
      config:
        import: optional:configserver:http://localhost:8888
    ```

### Ejecución

Desde la raíz del directorio `userservice`, ejecuta el siguiente comando Maven:

```bash
mvn spring-boot:run
```

El servicio se registrará en Eureka y estará disponible en el puerto `8081` (o el que se haya configurado).

**Nota Importante:** Aunque el servicio es accesible directamente en el puerto `8081` en un entorno de desarrollo, en producción, el acceso debe realizarse exclusivamente a través del **API Gateway** para garantizar la seguridad y la correcta gestión de las peticiones.

## 5. API Endpoints

A continuación se detallan los endpoints expuestos por el servicio.

---

### **Registrar un Nuevo Usuario**

-   **Endpoint**: `POST /api/users/register`
-   **Descripción**: Crea un nuevo usuario en el sistema.
-   **Request Body**:

    ```json
    {
      "email": "nuevo.usuario@example.com",
      "password": "passwordSeguro123",
      "firstName": "Nombre",
      "lastName": "Apellido",
      "keycloakId": "a1b2c3d4-e5f6-7890-1234-567890abcdef"
    }
    ```

-   **Response (200 OK)**:

    ```json
    {
      "id": "uuid-generado-por-la-db",
      "email": "nuevo.usuario@example.com",
      "firstName": "Nombre",
      "lastName": "Apellido",
      "keycloakId": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
      "createdAt": "2026-04-08T10:00:00.000000",
      "updatedAt": "2026-04-08T10:00:00.000000"
    }
    ```

---

### **Obtener Perfil de Usuario**

-   **Endpoint**: `GET /api/users/{userId}`
-   **Descripción**: Obtiene la información completa de un usuario a través de su `keycloakId`.
-   **Path Variable**:
    -   `userId`: El `keycloakId` del usuario.
-   **Response (200 OK)**:

    ```json
    {
      "id": "uuid-de-la-db",
      "email": "usuario.existente@example.com",
      "firstName": "Nombre",
      "lastName": "Apellido",
      "keycloakId": "{userId}",
      "createdAt": "2026-04-07T15:30:00.000000",
      "updatedAt": "2026-04-07T15:30:00.000000"
    }
    ```

---

### **Validar Existencia de Usuario**

-   **Endpoint**: `GET /api/users/{userId}/validate`
-   **Descripción**: Endpoint ligero para que otros servicios verifiquen si un usuario existe usando su `keycloakId`, sin necesidad de obtener toda la información.
-   **Path Variable**:
    -   `userId`: El `keycloakId` del usuario a validar.
-   **Response (200 OK)**:
    -   `true` si el usuario existe.
    -   `false` si el usuario no existe.

## 6. Modelo de Datos (Entidad `User`)

La tabla `users` en la base de datos tiene la siguiente estructura:

| Columna     | Tipo              | Restricciones        | Descripción                               |
| :---------- | :---------------- | :------------------- | :---------------------------------------- |
| `id`        | `VARCHAR(36)`     | **Clave Primaria**   | Identificador único (UUID).               |
| `email`     | `VARCHAR(255)`    | Único, No Nulo       | Dirección de correo electrónico del usuario. |
| `keycloakId`| `VARCHAR(255)`    |                      | ID del usuario en Keycloak.               |
| `password`  | `VARCHAR(255)`    | No Nulo              | Contraseña (debería estar hasheada).      |
| `firstName` | `VARCHAR(255)`    |                      | Nombre del usuario.                       |
| `lastName`  | `VARCHAR(255)`    |                      | Apellido del usuario.                     |
| `role`      | `VARCHAR(255)`    | Default: 'USER'      | Rol del usuario (ej. USER, ADMIN).        |
| `createdAt` | `TIMESTAMP`       |                      | Fecha y hora de creación del registro.    |
| `updatedAt` | `TIMESTAMP`       |                      | Fecha y hora de la última actualización.  |
