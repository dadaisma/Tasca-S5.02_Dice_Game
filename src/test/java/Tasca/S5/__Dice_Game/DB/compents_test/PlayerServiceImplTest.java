package Tasca.S5.__Dice_Game.DB.compents_test;


import Tasca.S5.__Dice_Game.DB.model.domain.Player;
import Tasca.S5.__Dice_Game.DB.model.dto.PlayerDTO;
import Tasca.S5.__Dice_Game.DB.model.repository.GameRepository;
import Tasca.S5.__Dice_Game.DB.model.repository.PlayerRepository;
import Tasca.S5.__Dice_Game.DB.model.service.PlayerServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
//@DataMongoTest
//@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class PlayerServiceImplTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private PlayerServiceImpl playerService;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);


        SecurityContextHolder.getContext().setAuthentication(authentication);

    }
    private void mockAuthentication(String roleAdmin) {
    }

    @Test
    public void testCreatePlayer_EmailEmpty() {
        // Given
        PlayerDTO playerDTO = new PlayerDTO();
        playerDTO.setEmail("");
        playerDTO.setPassword("password");
        playerDTO.setName("testPlayer");

        // When / Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            playerService.createPlayer(playerDTO);
        });
        assertEquals("Email cannot be empty", exception.getMessage());
    }

    @Test
    public void testCreatePlayer_InvalidEmailFormat() {
        // Given
        PlayerDTO playerDTO = new PlayerDTO();
        playerDTO.setEmail("invalid-email");
        playerDTO.setPassword("password");
        playerDTO.setName("testPlayer");

        // When / Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            playerService.createPlayer(playerDTO);
        });
        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    public void testCreatePlayer_PasswordEmpty() {
        // Given
        PlayerDTO playerDTO = new PlayerDTO();
        playerDTO.setEmail("test@example.com");
        playerDTO.setPassword("");
        playerDTO.setName("testPlayer");

        // When / Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            playerService.createPlayer(playerDTO);
        });
        assertEquals("Password cannot be empty", exception.getMessage());
    }

    @Test
    public void testGetPlayerById_NotFound() {

        // Given
        String id = "1";

        // When / Then
        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class, () -> {
            playerService.getPlayerById(id);
        });

        assertEquals("Get One Player Failed: Invalid player id: " + id + " -> DOESN'T EXIST in DataBase", entityNotFoundException.getMessage());
    }


    @Test
    public void testUpdatePlayerName_InsufficientAuthentication() {
        // Given
        String playerId = "player1";
        PlayerDTO playerDTO = new PlayerDTO();
        playerDTO.setName("updatedName");

        // Setting up the current user with insufficient authentication
        Player currentUser = new Player();
        currentUser.setId("otherPlayerId");

        // Directly setting up the context
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(currentUser, null, Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"))));

        // Simulating the player repository state by directly manipulating the repository (if using an in-memory DB or similar)
        Player playerToUpdate = new Player();
        playerToUpdate.setId(playerId);
        playerRepository.save(playerToUpdate);  // Assuming save persists the player in an in-memory DB or mock

        // When / Then
        InsufficientAuthenticationException exception = assertThrows(InsufficientAuthenticationException.class, () -> {
            playerService.updatePlayerName(playerId, playerDTO);
        });

        assertEquals("You don't have permissions to modify this player's data", exception.getMessage());
    }


    @Test
    public void testCalculateTotalPlayedGames() {
        // Given
        String playerId = "player1";
        when(gameRepository.countByPlayerId(playerId)).thenReturn(10L);

        // When
        long totalPlayedGames = playerService.calculateTotalPlayedGames(playerId);

        // Then
        assertEquals(10, totalPlayedGames);
    }

    @Test
    public void testCreatePlayer_WithInvalidEmail() {
        PlayerDTO playerDTO = new PlayerDTO("John Doe", "invalid.email", "password");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> playerService.createPlayer(playerDTO));
        assertEquals("Invalid email format", exception.getMessage());
    }
}
