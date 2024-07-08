package Tasca.S5.__Dice_Game.DB.compents_test;

import Tasca.S5.__Dice_Game.DB.model.domain.Player;
import Tasca.S5.__Dice_Game.DB.model.domain.Role;
import Tasca.S5.__Dice_Game.DB.model.dto.PlayerDTO;
import Tasca.S5.__Dice_Game.DB.model.repository.PlayerRepository;
import Tasca.S5.__Dice_Game.DB.model.service.PlayerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class PlayerServiceImplTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PlayerServiceImpl playerService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    public void testUpdatePlayerName_AdminAccess_Success() {
        // Prepare test data
        String playerId = "1";
        PlayerDTO playerDTO = new PlayerDTO("John Smith", "john.smith@example.com", "newPassword");

        // Mock authentication
        Player authenticatedPlayer = new Player("admin", "admin@example.com", "password", Role.ROLE_ADMIN);
        Authentication authentication = new UsernamePasswordAuthenticationToken(authenticatedPlayer, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Mock behavior of PlayerRepository.findById()
        Player existingPlayer = new Player("John Doe", "john.doe@example.com", "password", Role.ROLE_USER);
        when(playerRepository.findById(playerId)).thenReturn(Optional.of(existingPlayer));

        // Mock behavior of PasswordEncoder.encode()
        String encodedPassword = "encodedNewPassword";
        when(passwordEncoder.encode("newPassword")).thenReturn(encodedPassword);

        // Call the method under test
        PlayerDTO updatedPlayerDTO = playerService.updatePlayerName(playerId, playerDTO);

        // Assertions
        assertEquals("John Smith", updatedPlayerDTO.getName());
        assertEquals("john.smith@example.com", updatedPlayerDTO.getEmail());
    }

    // Add more test cases for other scenarios as needed

}
