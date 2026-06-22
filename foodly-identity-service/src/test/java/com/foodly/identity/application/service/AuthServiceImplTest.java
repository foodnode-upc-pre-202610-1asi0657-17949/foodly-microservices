package com.foodly.identity.application.service;

import com.foodly.identity.application.dto.AuthResponseDto;
import com.foodly.identity.application.dto.LoginRequestDto;
import com.foodly.identity.application.dto.RegisterRequestDto;
import com.foodly.identity.application.dto.UserCreatedEventDto;
import com.foodly.identity.application.dto.UserProfileDto;
import com.foodly.identity.application.exception.AuthenticationException;
import com.foodly.identity.application.exception.UserAlreadyExistsException;
import com.foodly.identity.application.exception.UserNotFoundException;
import com.foodly.identity.domain.model.User;
import com.foodly.identity.domain.model.UserRole;
import com.foodly.identity.infrastructure.messaging.UserEventPublisher;
import com.foodly.identity.infrastructure.persistence.UserRepository;
import com.foodly.identity.infrastructure.security.JwtProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private UserEventPublisher userEventPublisher;

    @InjectMocks
    private AuthServiceImpl authService;

    // ==========================================
    // PRUEBAS PARA: register()
    // ==========================================

    @Test
    void register_Success_WhenDataIsUnique() {
        // Arrange
        RegisterRequestDto request = new RegisterRequestDto();
        request.setEmail("jasmin@foodnode.pe");
        request.setUsername("jasmin_up");
        request.setPassword("Foodly2025!");
        request.setFirstName("Jasmin");
        request.setLastName("Urrutia");
        request.setRoles(Set.of(UserRole.CLIENT));

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            return u; // Retorna el usuario simulando el PrePersist/save
        });
        when(jwtProvider.generateToken(any(), anyString(), anySet())).thenReturn("mocked-jwt-token");

        // Act
        AuthResponseDto response = authService.register(request);

        // Assert
        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.getAccessToken());
        assertEquals("jasmin@foodnode.pe", response.getEmail());
        assertEquals("jasmin_up", response.getUsername());

        verify(userRepository, times(1)).save(any(User.class));
        verify(userEventPublisher, times(1)).publishUserCreatedEvent(any(UserCreatedEventDto.class));
    }

    @Test
    void register_ThrowsException_WhenEmailAlreadyExists() {
        // Arrange
        RegisterRequestDto request = new RegisterRequestDto();
        request.setEmail("duplicado@foodly.com");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // Act & Assert
        assertThrows(UserAlreadyExistsException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any());
        verifyNoInteractions(userEventPublisher);
    }

    // ==========================================
    // PRUEBAS PARA: login()
    // ==========================================

    @Test
    void login_Success_WithCorrectCredentials() {
        // Arrange
        LoginRequestDto request = new LoginRequestDto("diego@foodly.com", "validPassword123");

        String hashedPw = BCrypt.hashpw("validPassword123", BCrypt.gensalt(4)); // cost factor bajo para rapidez de test
        User mockUser = new User("diego@foodly.com", "diego_cacho", hashedPw, "Diego", "Cacho", Set.of(UserRole.CLIENT));
        mockUser.setActive(true);

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(mockUser));
        when(jwtProvider.generateToken(any(), eq(mockUser.getEmail()), eq(mockUser.getRoles()))).thenReturn("jwt-login-token");

        // Act
        AuthResponseDto response = authService.login(request);

        // Assert
        assertNotNull(response);
        assertEquals("jwt-login-token", response.getAccessToken());
        verify(jwtProvider, times(1)).generateToken(any(), anyString(), anySet());
    }

    @Test
    void login_ThrowsException_WhenUserIsInactive() {
        // Arrange
        LoginRequestDto request = new LoginRequestDto("inactivo@foodly.com", "password");
        User mockUser = new User("inactivo@foodly.com", "user_x", "hash", "X", "Y", Set.of(UserRole.CLIENT));
        mockUser.setActive(false); // Inactivo

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(mockUser));

        // Act & Assert
        AuthenticationException exception = assertThrows(AuthenticationException.class, () -> authService.login(request));
        assertTrue(exception.getMessage().contains("desactivada"));
    }

    @Test
    void login_ThrowsException_WhenPasswordIsIncorrect() {
        // Arrange
        LoginRequestDto request = new LoginRequestDto("sergio@foodly.com", "wrongPassword");
        String hashedPw = BCrypt.hashpw("correctPassword", BCrypt.gensalt(4));
        User mockUser = new User("sergio@foodly.com", "sergio_j", hashedPw, "Sergio", "Julca", Set.of(UserRole.CLIENT));

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(mockUser));

        // Act & Assert
        assertThrows(AuthenticationException.class, () -> authService.login(request));
    }

    // ==========================================
    // PRUEBAS PARA: getUserProfile()
    // ==========================================

    @Test
    void getUserProfile_Success_WhenUserExists() {
        // Arrange
        String userId = "uuid-111";
        User mockUser = new User("fabricio@foodly.com", "fabricio_v", "hash", "Fabricio", "Vega", Set.of(UserRole.CLIENT));

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        // Act
        UserProfileDto profile = authService.getUserProfile(userId);

        // Assert
        assertNotNull(profile);
        assertEquals("fabricio@foodly.com", profile.getEmail());
        assertEquals("Fabricio", profile.getFirstName());
    }

    @Test
    void getUserProfile_ThrowsException_WhenUserDoesNotExist() {
        // Arrange
        String invalidId = "non-existent";
        when(userRepository.findById(invalidId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> authService.getUserProfile(invalidId));
    }
}