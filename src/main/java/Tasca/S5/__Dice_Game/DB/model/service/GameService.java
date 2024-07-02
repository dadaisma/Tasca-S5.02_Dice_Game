package Tasca.S5.__Dice_Game.DB.model.service;

import Tasca.S5.__Dice_Game.DB.model.dto.GameDTO;
import Tasca.S5.__Dice_Game.DB.model.dto.PlayerDTO;

import java.util.List;

public interface GameService {

    GameDTO createGame(Long playerId);

    List<GameDTO> getGamesByPlayerId(Long playerId);

  //  void deletePlayerGames(Long playerId);

    List<GameDTO> getAllGameDtos();
}
