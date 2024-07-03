package Tasca.S5.__Dice_Game.DB.controllers;

import Tasca.S5.__Dice_Game.DB.model.dto.GameDTO;
import Tasca.S5.__Dice_Game.DB.model.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/players/{playerId}/games")
public class GameController {

    @Autowired
    private GameService gameService;

    @PostMapping
    public GameDTO createGame(@PathVariable String playerId) {
        // Create game in MySQL
        GameDTO gameDTO = gameService.createGame(playerId);

        // Update player's games array in MongoDB
        gameService.updatePlayerGamesInMongoDB(playerId);

        return gameDTO;
    }

    @GetMapping
    public List<GameDTO> getGamesByPlayerId(@PathVariable String playerId) {
        return gameService.getGamesByPlayerId(playerId);
    }

  //  @DeleteMapping
  //  public void deletePlayerGames(@PathVariable Long playerId) {
   //     gameService.deletePlayerGames(playerId);
  //  }
}
