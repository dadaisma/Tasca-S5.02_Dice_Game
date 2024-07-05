package Tasca.S5.__Dice_Game.DB.controllers;

import Tasca.S5.__Dice_Game.DB.model.dto.GameDTO;
import Tasca.S5.__Dice_Game.DB.model.service.GameService;
import Tasca.S5.__Dice_Game.DB.utils.HeaderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import static Tasca.S5.__Dice_Game.DB.utils.HeaderUtil.createHeaders;


@RestController
@RequestMapping("/players/{playerId}/games")
public class GameController {

    @Autowired
    private GameService gameService;


    String token = HeaderUtil.getToken();

    @PostMapping
    public ResponseEntity<GameDTO>  createGame(@PathVariable String playerId) {
        // Create game in MySQL
        GameDTO createGameDTO = gameService.createGame(playerId);

        return new ResponseEntity<>(createGameDTO, createHeaders(token), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<GameDTO>> getGamesByPlayerId(@PathVariable String playerId) {
        List<GameDTO> gamesByPlayerId = gameService.getGamesByPlayerId(playerId);
        return new ResponseEntity<>(gamesByPlayerId, createHeaders(token), HttpStatus.OK);
    }


    @DeleteMapping
    public ResponseEntity<Void> deletePlayerGames(@PathVariable String playerId) {
        gameService.deletePlayerGames(playerId);
        return new ResponseEntity<>(createHeaders(token), HttpStatus.NO_CONTENT);
    }
}
