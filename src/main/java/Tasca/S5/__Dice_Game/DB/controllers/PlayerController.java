package Tasca.S5.__Dice_Game.DB.controllers;

import Tasca.S5.__Dice_Game.DB.model.dto.GameDTO;
import Tasca.S5.__Dice_Game.DB.model.dto.PlayerDTO;
import Tasca.S5.__Dice_Game.DB.model.service.PlayerService;
import Tasca.S5.__Dice_Game.DB.utils.HeaderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static Tasca.S5.__Dice_Game.DB.utils.HeaderUtil.createHeaders;


@RestController
@RequestMapping("/players")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    String token = HeaderUtil.getToken();

    @PostMapping
    public ResponseEntity<PlayerDTO>createPlayer(@RequestBody PlayerDTO playerDTO) {
       PlayerDTO userCreated = playerService.createPlayer(playerDTO);
        return new ResponseEntity<>(userCreated, createHeaders(token), HttpStatus.CREATED) ;
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlayerDTO> updatePlayerName(@PathVariable String id, @RequestBody PlayerDTO playerDTO) {
        PlayerDTO editUser = playerService.updatePlayerName(id, playerDTO);

        return new ResponseEntity<>(editUser, createHeaders(HeaderUtil.getToken()), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<PlayerDTO>> getAllPlayers() {
        List<PlayerDTO> players = playerService.getAllPlayers();
        return new ResponseEntity<>(players, createHeaders(token), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerDTO> getPlayerById(@PathVariable String id) {
        PlayerDTO userById = playerService.getPlayerById(id);
        return new ResponseEntity<>(userById, createHeaders(token), HttpStatus.OK);
    }

    @GetMapping("/ranking")
    public ResponseEntity<String> getAverageSuccessRate() {
        String averageSuccessRate = playerService.getAverageSuccessRate();
        return new ResponseEntity<>(averageSuccessRate, createHeaders(token), HttpStatus.OK);
    }

    @GetMapping("/ranking/loser")
    public ResponseEntity<PlayerDTO> getPlayerWithLowestSuccessRate() {
        PlayerDTO loser = playerService.getPlayerWithLowestSuccessRate();
        return new ResponseEntity<>(loser, createHeaders(token), HttpStatus.OK);
    }

    @GetMapping("/ranking/winner")
    public ResponseEntity<PlayerDTO> getPlayerWithHighestSuccessRate() {
        PlayerDTO winner = playerService.getPlayerWithHighestSuccessRate();
        return new ResponseEntity<>(winner, createHeaders(token),HttpStatus.OK);
    }


}
