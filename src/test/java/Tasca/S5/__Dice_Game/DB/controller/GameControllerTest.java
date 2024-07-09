package Tasca.S5.__Dice_Game.DB.controller;

import Tasca.S5.__Dice_Game.DB.controllers.GameController;
import Tasca.S5.__Dice_Game.DB.model.dto.GameDTO;
import Tasca.S5.__Dice_Game.DB.model.service.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class GameControllerTest {

    @Mock
    private GameService gameService;

    @InjectMocks
    private GameController gameController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateGame_Success() {
        // Given
        String playerId = "player1";
        GameDTO mockGameDTO = new GameDTO(1L, 4, 3, true); // Example values

        // Mock the gameService.createGame method
        when(gameService.createGame(playerId)).thenReturn(mockGameDTO);

        // When
        ResponseEntity<GameDTO> response = gameController.createGame(playerId);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(mockGameDTO, response.getBody());
        verify(gameService, times(1)).createGame(eq(playerId));
    }

    @Test
    public void testGetGamesByPlayerId_Success() {
        // Given
        String playerId = "player1";
        List<GameDTO> mockGamesList = Collections.singletonList(new GameDTO(1L, 4, 3, true)); // Example values
        when(gameService.getGamesByPlayerId(playerId)).thenReturn(mockGamesList);

        // When
        ResponseEntity<List<GameDTO>> response = gameController.getGamesByPlayerId(playerId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockGamesList, response.getBody());
        verify(gameService, times(1)).getGamesByPlayerId(playerId);
    }
    @Test
    public void testDeletePlayerGames_Admin_Success() {
        // Given
        String playerId = "player1";
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("admin", null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))));

        // When
        ResponseEntity<Void> response = gameController.deletePlayerGames(playerId);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(gameService, times(1)).deletePlayerGames(playerId);
    }

    @Test
    public void testDeletePlayerGames_NonAdmin_Failure() {
        // Given
        String playerId = "player1";
        // Simulate authentication as a non-admin user
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                "user", null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // When/Then
        InsufficientAuthenticationException exception = assertThrows(InsufficientAuthenticationException.class, () -> {
            gameController.deletePlayerGames(playerId);
        });

        assertEquals("You don't have permissions to access this resource", exception.getMessage());
        verify(gameService, never()).deletePlayerGames(anyString());
    }

}
