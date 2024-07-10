package Tasca.S5.__Dice_Game.DB.compents_test;

import Tasca.S5.__Dice_Game.DB.model.domain.Game;
import Tasca.S5.__Dice_Game.DB.model.domain.Player;
import Tasca.S5.__Dice_Game.DB.model.domain.Role;
import Tasca.S5.__Dice_Game.DB.model.dto.PlayerDTO;
import Tasca.S5.__Dice_Game.DB.model.repository.GameRepository;
import Tasca.S5.__Dice_Game.DB.model.repository.PlayerRepository;
import Tasca.S5.__Dice_Game.DB.model.service.PlayerServiceImpl;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    public void testCreatePlayer_NameAlreadyExists() {
        // Given
        Player existingPlayer = new Player();
        existingPlayer.setName("existingPlayer");
        existingPlayer.setEmail("existing@example.com");
        playerRepository.save(existingPlayer);

        PlayerDTO playerDTO = new PlayerDTO();
        playerDTO.setEmail("test@example.com");
        playerDTO.setPassword("password");
        playerDTO.setName("existingPlayer");  // Same name as existing player

        // When / Then
        EntityExistsException exception = assertThrows(EntityExistsException.class, () -> {
            playerService.createPlayer(playerDTO);
        });

        assertEquals("Create new Player Failed: Invalid Player name: existingPlayer -> ALREADY EXISTS in DataBase", exception.getMessage());
    }


    /*
    @Test
    public void testUpdatePlayerName_Success() {
        // Given
        String id = "668bfc1228ec2a2113ae4d41";
        PlayerDTO playerDTO = new PlayerDTO();
        playerDTO.setName("updatedName");
        playerDTO.setEmail("updated@example.com");
        playerDTO.setPassword("newPassword");

        Player player = new Player();
        player.setId(id);
        player.setName("currentName");
        player.setEmail("current@example.com");
        player.setPassword("currentPassword");

        // Mocking authentication context
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(player);
      //  when(authentication.getAuthorities()).thenReturn(Arrays.asList((GrantedAuthority) () -> "ROLE_ADMIN"));

        // Set the mocked authentication into SecurityContextHolder
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Mocking repository behavior
        when(playerRepository.findById(id)).thenReturn(Optional.of(player));
        when(playerRepository.save(player)).thenReturn(player); // Simulate save operation

        // When
        PlayerDTO updatedPlayerDTO = playerService.updatePlayerName(id, playerDTO);

        // Then
        assertEquals("updatedName", updatedPlayerDTO.getName());
        assertEquals("updated@example.com", updatedPlayerDTO.getEmail());
        // Add more assertions as needed
    }


     */


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
    public void testGetPlayerById_Success() {
        // Given
        String playerId = "player1";
        Player player = new Player();
        player.setId(playerId);
        player.setName("testPlayer");
        player.setEmail("test@example.com");

        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player)); // Correct mock setup
        when(gameRepository.countByPlayerId(playerId)).thenReturn(10L);
        when(gameRepository.countByPlayerIdAndWon(playerId, true)).thenReturn(5L);

        // When
        PlayerDTO playerDTO = playerService.getPlayerById(playerId);

        // Then
        assertNotNull(playerDTO);
        assertEquals("testPlayer", playerDTO.getName());
        assertEquals("test@example.com", playerDTO.getEmail());
        assertEquals(50.0, playerDTO.getSuccessRate());
        assertEquals(10, playerDTO.getTotalPlayedGames());
    }

    @Test
    public void testGetAllPlayers_Success() {
        // Given
        Player player1 = new Player();
        player1.setName("testPlayer1");
        player1.setEmail("test1@example.com");
        playerRepository.save(player1);

        Player player2 = new Player();
        player2.setName("testPlayer2");
        player2.setEmail("test2@example.com");
        playerRepository.save(player2);



        // When
        List<PlayerDTO> players = playerService.getAllPlayers();

        // Then
        assertNotNull(players);
        assertEquals(2, players.size());

        PlayerDTO playerDTO1 = players.get(0);
        assertEquals("testPlayer1", playerDTO1.getName());
        assertEquals("test1@example.com", playerDTO1.getEmail());
   //     assertEquals(50.0, playerDTO1.getSuccessRate());
     //   assertEquals(10, playerDTO1.getTotalPlayedGames());

        PlayerDTO playerDTO2 = players.get(1);
        assertEquals("testPlayer2", playerDTO2.getName());
        assertEquals("test2@example.com", playerDTO2.getEmail());
     //   assertEquals(60.0, playerDTO2.getSuccessRate());  // Adjusted expected success rate to match the mock setup
      //  assertEquals(5, playerDTO2.getTotalPlayedGames());  // Adjusted expected total played games to match the mock setup
    }





    @Test
    public void testCreatePlayer_Success() {
        // Given
        PlayerDTO playerDTO = new PlayerDTO();
        playerDTO.setEmail("test@example.com");
        playerDTO.setPassword("password");
        playerDTO.setName("testPlayer");

        when(passwordEncoder.encode(playerDTO.getPassword())).thenReturn("encodedPassword");
        when(playerRepository.findByName(playerDTO.getName())).thenReturn(Optional.empty());
        when(playerRepository.findByEmail(playerDTO.getEmail())).thenReturn(Optional.empty());
        when(authentication.getAuthorities()).thenReturn((List) Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));

        // Mock setup for findAll() to return a list with mocked players
        Player player = new Player();
        player.setId("playerId");
        player.setName(playerDTO.getName());
        player.setEmail(playerDTO.getEmail());
        player.setPassword("encodedPassword");
        player.setRole(Role.ROLE_USER);

        when(playerRepository.save(any(Player.class))).thenReturn(player);
        when(playerRepository.findAll()).thenReturn(Arrays.asList(player)); // Adjust to return mock players list
        when(gameRepository.countByPlayerId(player.getId())).thenReturn(0L);

        // When
        PlayerDTO createdPlayerDTO = playerService.createPlayer(playerDTO);

        // Then
        assertNotNull(createdPlayerDTO);
        assertEquals("testPlayer", createdPlayerDTO.getName());
        assertEquals("test@example.com", createdPlayerDTO.getEmail());
        assertEquals(Role.ROLE_USER, createdPlayerDTO.getRole());
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
}
