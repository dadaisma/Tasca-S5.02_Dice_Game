package Tasca.S5.__Dice_Game.DB.compents_test;

import Tasca.S5.__Dice_Game.DB.model.domain.Player;
import Tasca.S5.__Dice_Game.DB.model.dto.GameDTO;
import Tasca.S5.__Dice_Game.DB.model.domain.Game;
import Tasca.S5.__Dice_Game.DB.model.repository.GameRepository;
import Tasca.S5.__Dice_Game.DB.model.repository.PlayerRepository;
import Tasca.S5.__Dice_Game.DB.model.service.GameServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.persistence.EntityNotFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class GameServiceImpTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private GameServiceImp gameService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    public void testCreateGame_Success() {
        // Given
        String playerId = "player1";
        Player player = new Player();
        player.setId(playerId);

        when(authentication.getPrincipal()).thenReturn(player);
        when(authentication.getAuthorities()).thenReturn((List) Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player));

        Game game = new Game();
        game.setId(1L);
        game.setDie1(3);
        game.setDie2(4);
        game.setWon(true);
        game.setPlayerId(playerId);

        when(gameRepository.save(any(Game.class))).thenReturn(game);

        // When
        GameDTO gameDTO = gameService.createGame(playerId);

        // Then
        assertNotNull(gameDTO);
        assertEquals(1L, gameDTO.getId());
        assertEquals(3, gameDTO.getDie1());
        assertEquals(4, gameDTO.getDie2());
        assertTrue(gameDTO.isWon());
        verify(gameRepository, times(1)).save(any(Game.class));
    }


    @Test
    public void testCreateGame_PlayerNotFound() {
        // Given
        String playerId = "player1";
        Player currentPlayer = new Player();
        currentPlayer.setId(playerId);

        when(authentication.getPrincipal()).thenReturn(currentPlayer);
        when(authentication.getAuthorities()).thenReturn((List) Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
        when(playerRepository.findById(playerId)).thenReturn(Optional.empty());

        // When / Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            gameService.createGame(playerId);
        });
        assertEquals("Create Game Failed: Player with ID " + playerId + " not found in database", exception.getMessage());
    }

    @Test
    public void testCreateGame_InsufficientAuthentication() {
        // Given
        String playerId = "player1";
        String anotherPlayerId = "player2";
        Player currentPlayer = new Player();
        currentPlayer.setId(anotherPlayerId);

        when(authentication.getPrincipal()).thenReturn(currentPlayer);
        when(authentication.getAuthorities()).thenReturn((List) Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
        when(playerRepository.findById(playerId)).thenReturn(Optional.of(new Player()));

        // When / Then
        InsufficientAuthenticationException exception = assertThrows(InsufficientAuthenticationException.class, () -> {
            gameService.createGame(playerId);
        });
        assertEquals("You don't have permissions to modify this player's data", exception.getMessage());
    }


    @Test
    public void testGetGamesByPlayerId_Success() {
        // Given
        String playerId = "player1";
        Player player = new Player();
        player.setId(playerId);

        Game game = new Game();
        game.setId(1L);
        game.setDie1(3);
        game.setDie2(4);
        game.setWon(true);
        game.setPlayerId(playerId);

        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player));
        when(gameRepository.findByPlayerId(playerId)).thenReturn(Optional.of(Arrays.asList(game)));

        // When
        List<GameDTO> gameDTOs = gameService.getGamesByPlayerId(playerId);

        // Then
        assertNotNull(gameDTOs);
        assertEquals(1, gameDTOs.size());
        assertEquals(1L, gameDTOs.get(0).getId());
        assertEquals(3, gameDTOs.get(0).getDie1());
        assertEquals(4, gameDTOs.get(0).getDie2());
        assertTrue(gameDTOs.get(0).isWon());
        verify(gameRepository, times(1)).findByPlayerId(playerId);
    }

    @Test
    public void testGetGamesByPlayerId_PlayerNotFound() {
        // Given
        String playerId = "player1";
        when(playerRepository.findById(playerId)).thenReturn(Optional.empty());

        // When / Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            gameService.getGamesByPlayerId(playerId);
        });
        assertEquals("Player with ID " + playerId + " not found in MongoDB", exception.getMessage());
    }

    @Test
    public void testDeletePlayerGames_Success() {
        // Given
        String playerId = "player1";
        Player player = new Player();
        player.setId(playerId);

        Game game = new Game();
        game.setId(1L);
        game.setDie1(3);
        game.setDie2(4);
        game.setWon(true);
        game.setPlayerId(playerId);

        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player));
        when(gameRepository.findByPlayerId(playerId)).thenReturn(Optional.of(Arrays.asList(game)));

        // When
        gameService.deletePlayerGames(playerId);

        // Then
        verify(gameRepository, times(1)).delete(any(Game.class));
    }

    @Test
    public void testDeletePlayerGames_PlayerNotFound() {
        // Given
        String playerId = "player1";
        when(playerRepository.findById(playerId)).thenReturn(Optional.empty());

        // When / Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            gameService.deletePlayerGames(playerId);
        });
        assertEquals("Player with ID " + playerId + " not found in MongoDB", exception.getMessage());
    }
}
