package Tasca.S5.__Dice_Game.DB;

import Tasca.S5.__Dice_Game.DB.model.domain.Player;
import Tasca.S5.__Dice_Game.DB.model.dto.PlayerDTO;
import Tasca.S5.__Dice_Game.DB.model.repository.PlayerRepository;
import Tasca.S5.__Dice_Game.DB.model.service.PlayerServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class PlayerServiceTest {

    @InjectMocks
    private PlayerServiceImpl playerService;

    @Mock
    private PlayerRepository playerRepository;

    public PlayerServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testUpdatePlayerName() {

        Player player = new Player("testUser", "test@example.com", "password", Role.USER);
        when(playerRepository.findById("1")).thenReturn(Optional.of(player));

        PlayerDTO playerDTO = new PlayerDTO();
        playerDTO.setName("newName");
        playerDTO.setEmail("new@example.com");
        playerDTO.setPassword("newPassword");

        PlayerDTO updatedPlayerDTO = playerService.updatePlayerName("1", playerDTO);

        assertThat(updatedPlayerDTO.getName()).isEqualTo("newName");
        assertThat(updatedPlayerDTO.getEmail()).isEqualTo("new@example.com");
    }
}
