package Tasca.S5.__Dice_Game.DB.controller;



import Tasca.S5.__Dice_Game.DB.controllers.PlayerController;
import Tasca.S5.__Dice_Game.DB.model.dto.PlayerDTO;
import Tasca.S5.__Dice_Game.DB.model.service.PlayerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class PlayerControllerTest {

    @Mock
    private PlayerService playerService;

    @InjectMocks
    private PlayerController playerController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreatePlayer_Admin_Success() {
        // Given
        PlayerDTO playerDTO = new PlayerDTO("Player One", "player1@example.com", "password");
        when(playerService.createPlayer(any(PlayerDTO.class))).thenReturn(playerDTO);

        // Mock authentication as ADMIN
        mockAuthentication("ROLE_ADMIN");

        // When
        ResponseEntity<PlayerDTO> response = playerController.createPlayer(playerDTO);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(playerDTO, response.getBody());
        verify(playerService, times(1)).createPlayer(eq(playerDTO));
    }

    private void mockAuthentication(String roleAdmin) {
    }



    @Test
    public void testUpdatePlayerName_Admin_Success() {
        // Given
        String playerId = "player1";
        PlayerDTO updatedPlayerDTO = new PlayerDTO( "Updated Player", "updated@example.com", "newpassword");
        when(playerService.updatePlayerName(eq(playerId), any(PlayerDTO.class))).thenReturn(updatedPlayerDTO);

        // Mock authentication as ADMIN
        mockAuthentication("ROLE_ADMIN");

        // When
        ResponseEntity<PlayerDTO> response = playerController.updatePlayerName(playerId, updatedPlayerDTO);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedPlayerDTO, response.getBody());
        verify(playerService, times(1)).updatePlayerName(eq(playerId), eq(updatedPlayerDTO));
    }

    @Test
    public void testUpdatePlayerName_NonAdmin_Self_Success() {
        // Given
        String playerId = "player1";
        PlayerDTO updatedPlayerDTO = new PlayerDTO( "Updated Player", "updated@example.com", "newpassword");
        when(playerService.updatePlayerName(eq(playerId), any(PlayerDTO.class))).thenReturn(updatedPlayerDTO);

        // Mock authentication as the player themselves
        mockAuthentication(playerId);

        // When
        ResponseEntity<PlayerDTO> response = playerController.updatePlayerName(playerId, updatedPlayerDTO);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedPlayerDTO, response.getBody());
        verify(playerService, times(1)).updatePlayerName(eq(playerId), eq(updatedPlayerDTO));
    }

    @Test
    public void testUpdatePlayerName_NonAdmin_Other_Failure() {
        // Given
        String playerId = "player2";
        PlayerDTO updatedPlayerDTO = new PlayerDTO("Updated Player", "updated@example.com", "newpassword");

        // Mock authentication as a different non-admin user
        mockAuthentication("otherUser");

        // When
        InsufficientAuthenticationException exception = assertThrows(InsufficientAuthenticationException.class, () -> {
            playerController.updatePlayerName(playerId, updatedPlayerDTO);
        });

        // Then
        assertEquals("You don't have permissions to modify this player's data", exception.getMessage());
        verify(playerService).updatePlayerName(anyString(), any(PlayerDTO.class));
    }


    @Test
    public void testGetAllPlayers_Admin_Success() {
        // Given
        List<PlayerDTO> playerDTOList = List.of(
                new PlayerDTO( "Player One", "player1@example.com", "password"),
                new PlayerDTO( "Player Two", "player2@example.com", "password")
        );
        when(playerService.getAllPlayers()).thenReturn(playerDTOList);

        // Mock authentication as ADMIN
        mockAuthentication("ROLE_ADMIN");

        // When
        ResponseEntity<List<PlayerDTO>> response = playerController.getAllPlayers(mock(HttpServletRequest.class));

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(playerDTOList, response.getBody());
        verify(playerService, times(1)).getAllPlayers();
    }

    @Test
    public void testGetPlayerById_Admin_Success() {
        // Given
        String playerId = "player1";
        PlayerDTO playerDTO = new PlayerDTO( "Player One", "player1@example.com", "password");
        when(playerService.getPlayerById(eq(playerId))).thenReturn(playerDTO);

        // Mock authentication as ADMIN
        mockAuthentication("ROLE_ADMIN");

        // When
        ResponseEntity<PlayerDTO> response = playerController.getPlayerById(playerId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(playerDTO, response.getBody());
        verify(playerService, times(1)).getPlayerById(eq(playerId));
    }

    @Test
    public void testGetAverageSuccessRate_Admin_Success() {
        // Given
        String successRate = "50%";
        when(playerService.getAverageSuccessRate()).thenReturn(successRate);

        // Mock authentication as ADMIN
        mockAuthentication("ROLE_ADMIN");

        // When
        ResponseEntity<String> response = playerController.getAverageSuccessRate();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(successRate, response.getBody());
        verify(playerService, times(1)).getAverageSuccessRate();
    }

    @Test
    public void testGetPlayerWithLowestSuccessRate_Admin_Success() {
        // Given
        PlayerDTO loser = new PlayerDTO( "Player One", "player1@example.com", "password");
        when(playerService.getPlayerWithLowestSuccessRate()).thenReturn(loser);

        // Mock authentication as ADMIN
        mockAuthentication("ROLE_ADMIN");

        // When
        ResponseEntity<PlayerDTO> response = playerController.getPlayerWithLowestSuccessRate();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(loser, response.getBody());
        verify(playerService, times(1)).getPlayerWithLowestSuccessRate();
    }

    @Test
    public void testGetPlayerWithHighestSuccessRate_Admin_Success() {
        // Given
        PlayerDTO winner = new PlayerDTO( "Player One", "player1@example.com", "password");
        when(playerService.getPlayerWithHighestSuccessRate()).thenReturn(winner);

        // Mock authentication as ADMIN
        mockAuthentication("ROLE_ADMIN");

        // When
        ResponseEntity<PlayerDTO> response = playerController.getPlayerWithHighestSuccessRate();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(winner, response.getBody());
        verify(playerService, times(1)).getPlayerWithHighestSuccessRate();
    }

    /*
    private void mockAuthentication(String role) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(role)));
        SecurityContext securityContext = mock(SecurityContext.class);
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    private void mockAuthentication(String playerId, boolean isAdmin) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(playerId);
        if (isAdmin) {
            when(authentication.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
        } else {
            when(authentication.getAuthorities()).thenReturn(Collections.emptyList()); // Non-admin user
        }
        SecurityContext securityContext = mock(SecurityContext.class);
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

     */
    @Test
    public void testCreatePlayer_NonAdmin_Failure() {
        // Given
        PlayerDTO playerDTO = new PlayerDTO("Player One", "player1@example.com", "password");

        // Mock authentication as non-admin
        mockAuthentication("ROLE_USER");

        // When
        InsufficientAuthenticationException exception = assertThrows(InsufficientAuthenticationException.class, () -> {
            playerController.createPlayer(playerDTO);
        });

        // Then
        assertEquals("You don't have permissions to access this resource", exception.getMessage());
        verify(playerService).createPlayer(any(PlayerDTO.class));
    }


}
