package Tasca.S5.__Dice_Game.DB.controllers;

import Tasca.S5.__Dice_Game.DB.model.dto.GameDTO;
import Tasca.S5.__Dice_Game.DB.model.dto.PlayerDTO;
import Tasca.S5.__Dice_Game.DB.model.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/players")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    @PostMapping
    public PlayerDTO createPlayer(@RequestBody PlayerDTO playerDTO) {
        return playerService.createPlayer(playerDTO);
    }

    @PutMapping("/{id}")
    public PlayerDTO updatePlayerName(@PathVariable String id, @RequestBody PlayerDTO playerDTO) {
        return playerService.updatePlayerName(id, playerDTO);
    }

    @GetMapping
    public List<PlayerDTO> getAllPlayers() {
        return playerService.getAllPlayers();
    }

    @GetMapping("/{id}")
    public PlayerDTO getPlayerById(@PathVariable String id) {
        return playerService.getPlayerById(id);
    }

    @GetMapping("/ranking")
    public String getAverageSuccessRate() {
        return playerService.getAverageSuccessRate();
    }

    @GetMapping("/ranking/loser")
    public PlayerDTO getPlayerWithLowestSuccessRate() {
        return playerService.getPlayerWithLowestSuccessRate();
    }

    @GetMapping("/ranking/winner")
    public PlayerDTO getPlayerWithHighestSuccessRate() {
        return playerService.getPlayerWithHighestSuccessRate();
    }


}
