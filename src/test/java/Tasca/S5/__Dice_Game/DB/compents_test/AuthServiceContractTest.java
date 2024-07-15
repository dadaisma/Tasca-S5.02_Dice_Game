package Tasca.S5.__Dice_Game.DB.compents_test;

import Tasca.S5.__Dice_Game.DB.dao.LoginRequest;
import Tasca.S5.__Dice_Game.DB.dao.RegisterRequest;
import Tasca.S5.__Dice_Game.DB.dao.response.JwtAuthenticationResponse;
import Tasca.S5.__Dice_Game.DB.model.domain.Player;
import Tasca.S5.__Dice_Game.DB.model.repository.PlayerRepository;
import Tasca.S5.__Dice_Game.DB.model.service.AuthService;
import Tasca.S5.__Dice_Game.DB.security.JwtService;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class AuthServiceContractTest {

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
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLogin_ValidCredentials() {
        // Given
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");

        // Mock player and userDetails
        Player player = Player.builder()
                .email(request.getEmail())
                .password("encodedPassword")
                .build();

        UserDetails userDetails = player;

        when(playerRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(player));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(jwtService.getToken(userDetails)).thenReturn("mocked_token");

        // When
        JwtAuthenticationResponse response = authService.login(request);

        // Then
        assertNotNull(response);
        assertEquals("mocked_token", response.getToken());
        assertNotNull(response.getExpiresIn());
        verify(playerRepository, times(1)).findByEmail(request.getEmail());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, times(1)).getToken(userDetails);
    }

    @Test
    public void testLogin_InvalidCredentials() {
        // Given
        LoginRequest request = new LoginRequest();
        request.setEmail("nonexistent@example.com");
        request.setPassword("invalid_password");

        when(playerRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        // When / Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->  {
            authService.login(request);
        });
        String expectedMessage = "Invalid email or password";
        assertEquals(expectedMessage, exception.getMessage());
    }




    @Test
    public void testSignUp_Success() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");
        request.setName("testPlayer");

        when(playerRepository.findByEmail(request.getEmail())).thenReturn(java.util.Optional.empty());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(jwtService.getToken(any(Player.class))).thenReturn("mocked_token");

        // When
        JwtAuthenticationResponse response = authService.signUp(request);

        // Then
        assertNotNull(response);
        assertEquals("mocked_token", response.getToken());
        assertNotNull(response.getExpiresIn());
        verify(playerRepository, times(1)).findByEmail(request.getEmail());
        verify(playerRepository, times(1)).save(any(Player.class));
        verify(jwtService, times(1)).getToken(any(Player.class));
    }

    @Test
    public void testSignUp_PlayerWithEmailAlreadyExists() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@example.com");
        request.setPassword("password");
        request.setName("existingPlayer");

        when(playerRepository.findByEmail(request.getEmail())).thenReturn(java.util.Optional.of(new Player()));

        // When / Then
        EntityExistsException exception = assertThrows(EntityExistsException.class, () -> {
            authService.signUp(request);
        });
        assertEquals("Player with email " + request.getEmail() + " already exists.", exception.getMessage());
        verify(playerRepository, times(1)).findByEmail(request.getEmail());
        verify(playerRepository, never()).save(any(Player.class));
        verify(jwtService, never()).getToken(any(Player.class));
    }
}
