package Tasca.S5.__Dice_Game.DB.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import Tasca.S5.__Dice_Game.DB.dao.LoginRequest;
import Tasca.S5.__Dice_Game.DB.dao.RegisterRequest;
import Tasca.S5.__Dice_Game.DB.dao.response.JwtAuthenticationResponse;
import Tasca.S5.__Dice_Game.DB.model.service.AuthService;
import Tasca.S5.__Dice_Game.DB.security.JwtService;
import Tasca.S5.__Dice_Game.DB.controllers.AuthenticationController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


public class AuthenticationControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthenticationController authenticationController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLogin_Success() {
        // Given
        LoginRequest request = new LoginRequest("username", "password");
        JwtAuthenticationResponse mockResponse = new JwtAuthenticationResponse("token", 3600);
        when(authService.login(any(LoginRequest.class))).thenReturn(mockResponse);

        // When
        ResponseEntity<JwtAuthenticationResponse> responseEntity = authenticationController.login(request);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("token", responseEntity.getBody().getToken());
        verify(authService, times(1)).login(request);
    }


    @Test
    public void testSignUp_Success() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing-email@example.com"); // Assume this email already exists
        request.setPassword("password");
        request.setName("username");
        JwtAuthenticationResponse mockResponse = new JwtAuthenticationResponse("token", 3600);
        when(authService.signUp(any(RegisterRequest.class))).thenReturn(mockResponse);

        // When
        ResponseEntity<JwtAuthenticationResponse> responseEntity = authenticationController.signUp(request);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("token", responseEntity.getBody().getToken());
        verify(authService, times(1)).signUp(request);
    }


    @Test
    public void testLogout_Success() {
        // Given
        String authorizationHeader = "Bearer token";
        ResponseEntity<String> expectedResponse = new ResponseEntity<>("Logged out successfully.", HttpStatus.OK);
        when(authService.logout(authorizationHeader)).thenReturn(expectedResponse);

        // When
        ResponseEntity<String> responseEntity = authenticationController.logout(authorizationHeader);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Logged out successfully.", responseEntity.getBody());
        verify(authService, times(1)).logout(authorizationHeader);
    }


    @Test
    public void testLogout_AuthServiceThrowsUnauthorizedException() {
        // Given
        String validAuthorizationHeader = "Bearer expiredToken";
        when(authService.logout(validAuthorizationHeader)).thenReturn(new ResponseEntity<>("Invalid or expired token.", HttpStatus.UNAUTHORIZED));

        // When
        ResponseEntity<String> responseEntity = authenticationController.logout(validAuthorizationHeader);

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        assertEquals("Invalid or expired token.", responseEntity.getBody());
    }
}