package Tasca.S5.__Dice_Game.DB.compents_test;

import Tasca.S5.__Dice_Game.DB.dao.LoginRequest;
import Tasca.S5.__Dice_Game.DB.dao.RegisterRequest;
import Tasca.S5.__Dice_Game.DB.dao.response.JwtAuthenticationResponse;
import Tasca.S5.__Dice_Game.DB.model.domain.Player;
import Tasca.S5.__Dice_Game.DB.model.domain.Role;
import Tasca.S5.__Dice_Game.DB.model.repository.PlayerRepository;
import Tasca.S5.__Dice_Game.DB.model.service.AuthService;
import Tasca.S5.__Dice_Game.DB.security.JwtService;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLogin_Success() {
        // Given
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");

        Player user = Player.builder().email("test@example.com").password("encodedPassword").build();
        when(playerRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.getToken(any(UserDetails.class))).thenReturn("mockToken");
        when(jwtService.getExpirationTime()).thenReturn(3600L);

        // When
        JwtAuthenticationResponse response = authService.login(request);

        // Then
        assertNotNull(response);
        assertEquals("mockToken", response.getToken());
        assertEquals(3600L, response.getExpiresIn());
    }

    @Test
    public void testLogin_Failure_InvalidCredentials() {
        // Given
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("wrongPassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new IllegalArgumentException("Invalid email or password"));

        // When / Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.login(request);
        });
        assertEquals("Invalid email or password", exception.getMessage());
    }

    @Test
    public void testSignUp_Success() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setEmail("newuser@example.com");
        request.setPassword("password");
        request.setName("newUser");

        when(playerRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(jwtService.getToken(any(Player.class))).thenReturn("mockToken");
        when(jwtService.getExpirationTime()).thenReturn(3600L);

        // When
        JwtAuthenticationResponse response = authService.signUp(request);

        // Then
        assertNotNull(response);
        assertEquals("mockToken", response.getToken());
        assertEquals(3600L, response.getExpiresIn());
        verify(playerRepository, times(1)).save(any(Player.class));
    }

    @Test
    public void testSignUp_Failure_EmailExists() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@example.com");
        request.setPassword("password");
        request.setName("existingUser");

        when(playerRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(new Player()));

        // When / Then
        EntityExistsException exception = assertThrows(EntityExistsException.class, () -> {
            authService.signUp(request);
        });
        assertEquals("Player with email existing@example.com already exists.", exception.getMessage());
        verify(playerRepository, never()).save(any(Player.class));
    }

    @Test
    public void testSignUp_Failure_InvalidEmailFormat() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setEmail("invalid-email");
        request.setPassword("password");
        request.setName("invalidEmailUser");

        // When / Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.signUp(request);
        });
        assertEquals("Invalid email format", exception.getMessage());
        verify(playerRepository, never()).save(any(Player.class));
    }
}
