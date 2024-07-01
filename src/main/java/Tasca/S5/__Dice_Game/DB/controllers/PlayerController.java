package Tasca.S5.__Dice_Game.DB.controllers;


import Tasca.S5.__Dice_Game.DB.model.domain.Player;
import Tasca.S5.__Dice_Game.DB.model.dto.PlayerDTO;
import Tasca.S5.__Dice_Game.DB.model.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

        import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/players")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    @PostMapping
    public PlayerDTO createPlayer(@RequestBody Player player) {
        if (player.getName() == null || player.getName().isEmpty()) {
            player.setName("ANÒNIM");
        }
        Player createdPlayer = playerService.createPlayer(player);
        return new PlayerDTO(createdPlayer.getId(), createdPlayer.getName(), createdPlayer.getRegistrationDate(), calculateSuccessRate(createdPlayer));
    }

    @PutMapping("/{id}")
    public PlayerDTO updatePlayerName(@PathVariable Long id, @RequestBody String name) {
        Player updatedPlayer = playerService.updatePlayerName(id, name);
        if (updatedPlayer != null) {
            return new PlayerDTO(updatedPlayer.getId(), updatedPlayer.getName(), updatedPlayer.getRegistrationDate(), calculateSuccessRate(updatedPlayer));
        }
        return null;
    }

    @GetMapping
    public List<PlayerDTO> getAllPlayers() {
        return playerService.getAllPlayers().stream()
                .map(player -> new PlayerDTO(player.getId(), player.getName(), player.getRegistrationDate(), calculateSuccessRate(player)))
                .collect(Collectors.toList());
    }

    private double calculateSuccessRate(Player player) {
        long totalGames = player.getGames().size();
        long wonGames = player.getGames().stream().filter(Game::isWon).count();
        return totalGames == 0 ? 0 : (double) wonGames / totalGames * 100;
    }
}
