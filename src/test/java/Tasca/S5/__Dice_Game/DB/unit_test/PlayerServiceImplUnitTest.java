package Tasca.S5.__Dice_Game.DB.unit_test;



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
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PlayerServiceImplUnitTest {

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
    }

    @Test
    public void testCreatePlayer_Success() {
        PlayerDTO playerDTO = new PlayerDTO("John Doe", "john.doe@example.com", "password");

        when(playerRepository.findByName(anyString())).thenReturn(Optional.empty());
        when(playerRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(authentication.getPrincipal()).thenReturn(new Player("admin", "admin@example.com", "password", Role.ROLE_ADMIN));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(playerRepository.save(any(Player.class))).thenReturn(new Player("John Doe", "john.doe@example.com", "encodedPassword", Role.ROLE_USER));

        PlayerDTO createdPlayerDTO = playerService.createPlayer(playerDTO);

        assertNotNull(createdPlayerDTO);
        assertEquals("John Doe", createdPlayerDTO.getName());
        assertEquals("john.doe@example.com", createdPlayerDTO.getEmail());
        assertEquals(Role.ROLE_USER, createdPlayerDTO.getRole());
    }

    @Test
    public void testCreatePlayer_WithExistingName() {
        PlayerDTO playerDTO = new PlayerDTO("ExistingPlayer", "existing.player@example.com", "password");

        when(playerRepository.findByName("ExistingPlayer")).thenReturn(Optional.of(new Player("ExistingPlayer", "existing.player@example.com", "password", Role.ROLE_USER)));

        EntityExistsException exception = assertThrows(EntityExistsException.class, () -> playerService.createPlayer(playerDTO));
        assertEquals("Create new Player Failed: Invalid Player name: ExistingPlayer -> ALREADY EXISTS in DataBase", exception.getMessage());
    }

    @Test
    public void testCreatePlayer_WithInvalidEmail() {
        PlayerDTO playerDTO = new PlayerDTO("John Doe", "invalid.email", "password");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> playerService.createPlayer(playerDTO));
        assertEquals("Invalid email format", exception.getMessage());
    }

    // Add more test cases as needed

}
