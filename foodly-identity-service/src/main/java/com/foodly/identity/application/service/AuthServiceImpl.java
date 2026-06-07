package com.foodly.identity.application.service;


import com.foodly.identity.application.dto.*;
import com.foodly.identity.application.exception.AuthenticationException;
import com.foodly.identity.application.exception.UserAlreadyExistsException;
import com.foodly.identity.application.exception.UserNotFoundException;
import com.foodly.identity.domain.model.User;
import com.foodly.identity.domain.model.UserRole;
import com.foodly.identity.infrastructure.messaging.UserEventPublisher;
import com.foodly.identity.infrastructure.persistence.UserRepository;
import com.foodly.identity.infrastructure.security.JwtProvider;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
@Transactional
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
    private static final int BCRYPT_COST_FACTOR = 12;
    private static final long DEFAULT_EXPIRATION_MINUTES = 60;

    @Inject
    private UserRepository userRepository;

    @Inject
    private JwtProvider jwtProvider;

    @Inject
    private UserEventPublisher userEventPublisher;

    @Override
    public AuthResponseDto register(RegisterRequestDto request) {
        log.info("[AuthService] Iniciando registro para email={}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("El email '" + request.getEmail() + "' ya está registrado en Foodly.");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("El nombre de usuario '" + request.getUsername() + "' ya está en uso.");
        }

        String passwordHash = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt(BCRYPT_COST_FACTOR));

        Set<UserRole> roles = (request.getRoles() != null && !request.getRoles().isEmpty())
                ? request.getRoles()
                : new HashSet<>(Set.of(UserRole.CLIENT));

        User user = new User(
                request.getEmail(),
                request.getUsername(),
                passwordHash,
                request.getFirstName(),
                request.getLastName(),
                roles
        );
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }

        User savedUser = userRepository.save(user);
        log.info("[AuthService] Usuario persistido: id={}", savedUser.getId());

        UserCreatedEventDto event = buildUserCreatedEvent(savedUser, "USER_CREATED");
        userEventPublisher.publishUserCreatedEvent(event);

        String token = jwtProvider.generateToken(savedUser.getId(), savedUser.getEmail(), savedUser.getRoles());

        log.info("[AuthService] Registro exitoso para userId={}", savedUser.getId());
        return buildAuthResponse(token, savedUser);
    }

    @Override
    public AuthResponseDto login(LoginRequestDto request) {
        log.info("[AuthService] Intento de login para email={}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthenticationException("Credenciales inválidas. Verifica tu email y contraseña."));

        if (!user.isActive()) {
            throw new AuthenticationException("Tu cuenta ha sido desactivada. Contacta soporte.");
        }

        if (!BCrypt.checkpw(request.getPassword(), user.getPasswordHash())) {
            log.warn("[AuthService] Contraseña incorrecta para email={}", request.getEmail());
            throw new AuthenticationException("Credenciales inválidas. Verifica tu email y contraseña.");
        }

        String token = jwtProvider.generateToken(user.getId(), user.getEmail(), user.getRoles());

        log.info("[AuthService] Login exitoso para userId={}", user.getId());
        return buildAuthResponse(token, user);
    }

    @Override
    public UserProfileDto getUserProfile(String userId) {
        log.debug("[AuthService] Consultando perfil para userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con id: " + userId));

        return mapToProfileDto(user);
    }

    private AuthResponseDto buildAuthResponse(String token, User user) {
        List<String> roleNames = user.getRoles().stream()
                .map(UserRole::name)
                .collect(Collectors.toList());

        return AuthResponseDto.builder()
                .accessToken(token)
                .expiresInMinutes(DEFAULT_EXPIRATION_MINUTES)
                .userId(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .roles(roleNames)
                .build();
    }

    private UserCreatedEventDto buildUserCreatedEvent(User user, String eventType) {
        List<String> roleNames = user.getRoles().stream()
                .map(UserRole::name)
                .collect(Collectors.toList());

        UserCreatedEventDto event = new UserCreatedEventDto();
        event.setEventId("evt-" + UUID.randomUUID());
        event.setEventType(eventType);
        event.setOccurredAt(Instant.now());
        event.setSourceService("foodly-identity-service");
        event.setUserId(user.getId());
        event.setEmail(user.getEmail());
        event.setUsername(user.getUsername());
        event.setFirstName(user.getFirstName());
        event.setLastName(user.getLastName());
        event.setPhoneNumber(user.getPhoneNumber());
        event.setRoles(roleNames);
        event.setActive(user.isActive());
        return event;
    }

    private UserProfileDto mapToProfileDto(User user) {
        List<String> roleNames = user.getRoles().stream()
                .map(UserRole::name)
                .collect(Collectors.toList());

        return new UserProfileDto(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber(),
                roleNames,
                user.isActive(),
                user.isEmailVerified()
        );
    }
}