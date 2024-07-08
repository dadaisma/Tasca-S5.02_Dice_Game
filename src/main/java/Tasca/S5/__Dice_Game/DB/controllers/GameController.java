package Tasca.S5.__Dice_Game.DB.controllers;

import Tasca.S5.__Dice_Game.DB.model.dto.GameDTO;
import Tasca.S5.__Dice_Game.DB.model.service.GameService;
import Tasca.S5.__Dice_Game.DB.utils.HeaderUtil;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import static Tasca.S5.__Dice_Game.DB.utils.HeaderUtil.createHeaders;


@RestController
@RequestMapping("/players/{playerId}/games")
public class GameController {

    @Autowired
    private GameService gameService;


    String token = HeaderUtil.getToken();

    @Operation(summary = "roll the die, user can play for himself while ADMIN can play for anyone")
    @PostMapping
    public ResponseEntity<GameDTO>  createGame(@PathVariable String playerId) {
        // Create game in MySQL
        GameDTO createGameDTO = gameService.createGame(playerId);

        return new ResponseEntity<>(createGameDTO, createHeaders(token), HttpStatus.CREATED);
    }

    @Operation(summary = "GET played games by ID")
    @GetMapping
    public ResponseEntity<List<GameDTO>> getGamesByPlayerId(@PathVariable String playerId) {
        List<GameDTO> gamesByPlayerId = gameService.getGamesByPlayerId(playerId);
        return new ResponseEntity<>(gamesByPlayerId, createHeaders(token), HttpStatus.OK);
    }


    @Operation(summary = "ADMIN can delete played games of user/s")
    @DeleteMapping
    public ResponseEntity<Void> deletePlayerGames(@PathVariable String playerId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new InsufficientAuthenticationException("You don't have permissions to access this resource");
        }
        gameService.deletePlayerGames(playerId);
        return new ResponseEntity<>(createHeaders(token), HttpStatus.NO_CONTENT);
    }
}
