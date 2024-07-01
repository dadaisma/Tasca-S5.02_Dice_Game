package Tasca.S5.__Dice_Game.DB.model.service;

import Tasca.S5.__Dice_Game.DB.model.dto.GameDTO;
import Tasca.S5.__Dice_Game.DB.model.dto.PlayerDTO;

import java.util.List;

public interface GameService {

    void createGame(GameDTO gameDTO);

    GameDTO getGamesByPlayerId(Long id);



    List<GameDTO> getAllGameDtos ();

}
