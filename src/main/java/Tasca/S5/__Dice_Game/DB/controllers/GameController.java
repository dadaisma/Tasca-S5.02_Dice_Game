package Tasca.S5.__Dice_Game.DB.controllers;

import Tasca.S5.__Dice_Game.DB.model.domain.Game;
import Tasca.S5.__Dice_Game.DB.model.dto.GameDTO;
import Tasca.S5.__Dice_Game.DB.model.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/players/{playerId}/games")
public class GameController {

    @Autowired
    private GameService gameService;

    @PostMapping
    public GameDTO createGame(@PathVariable Long playerId) {
        Game createdGame = gameService.createGame(playerId);
        if (createdGame != null) {
            return new GameDTO(createdGame.getId(), createdGame.getDie1(), createdGame.getDie2(), createdGame.isWon());
        }
        return null;
    }

    @GetMapping
    public List<GameDTO> getGamesByPlayerId(@PathVariable Long playerId) {
        return gameService.getGamesByPlayerId(playerId).stream()
                .map(game -> new GameDTO(game.getId(), game.getDie1(), game.getDie2(), game.isWon()))
                .collect(Collectors.toList());
    }

    @DeleteMapping
    public void deletePlayerGames(@PathVariable Long playerId) {
        gameService.deletePlayerGames(playerId);
    }
}
