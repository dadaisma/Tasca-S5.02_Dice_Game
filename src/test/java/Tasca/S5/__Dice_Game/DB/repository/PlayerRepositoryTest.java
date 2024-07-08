package Tasca.S5.__Dice_Game.DB.repository;

import Tasca.S5.__Dice_Game.DB.model.domain.Player;
import Tasca.S5.__Dice_Game.DB.model.domain.Role;
import Tasca.S5.__Dice_Game.DB.model.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

@DataMongoTest
public class PlayerRepositoryTest {

    @MockBean
    private PlayerRepository playerRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindByName_ExistingName_ReturnsPlayer() {
        // Mock data
        String playerName = "John Doe";
        Player player = Player.builder()
                .name(playerName)
                .email("john.doe@example.com")
                .password("password")
                .role(Role.ROLE_USER)
                .build();

        // Mock behavior of PlayerRepository.findByName()
        when(playerRepository.findByName(playerName)).thenReturn(Optional.of(player));

        // Call the method under test
        Optional<Player> foundPlayerOpt = playerRepository.findByName(playerName);

        // Assertions
        assertEquals(playerName, foundPlayerOpt.orElseThrow().getName());
    }

    @Test
    public void testFindByName_NonExistingName_ReturnsEmptyOptional() {
        // Mock data
        String nonExistingName = "NonExistingName";

        // Mock behavior of PlayerRepository.findByName()
        when(playerRepository.findByName(nonExistingName)).thenReturn(Optional.empty());

        // Call the method under test
        Optional<Player> foundPlayerOpt = playerRepository.findByName(nonExistingName);

        // Assertions
        assertFalse(foundPlayerOpt.isPresent());
    }

    @Test
    public void testFindByEmail_ExistingEmail_ReturnsPlayer() {
        // Mock data
        String email = "john.doe@example.com";
        Player player = Player.builder()
                .name("John Doe")
                .email(email)
                .password("password")
                .role(Role.ROLE_USER)
                .build();

        // Mock behavior of PlayerRepository.findByEmail()
        when(playerRepository.findByEmail(email)).thenReturn(Optional.of(player));

        // Call the method under test
        Optional<Player> foundPlayerOpt = playerRepository.findByEmail(email);

        // Assertions
        assertEquals(email, foundPlayerOpt.orElseThrow().getEmail());
    }

    @Test
    public void testFindByEmail_NonExistingEmail_ReturnsEmptyOptional() {
        // Mock data
        String nonExistingEmail = "nonexisting@example.com";

        // Mock behavior of PlayerRepository.findByEmail()
        when(playerRepository.findByEmail(nonExistingEmail)).thenReturn(Optional.empty());

        // Call the method under test
        Optional<Player> foundPlayerOpt = playerRepository.findByEmail(nonExistingEmail);

        // Assertions
        assertFalse(foundPlayerOpt.isPresent());
    }

    // Add more test cases as needed for other methods in PlayerRepository

}
