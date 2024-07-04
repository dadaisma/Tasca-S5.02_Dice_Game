package Tasca.S5.__Dice_Game.DB.model.service;

import Tasca.S5.__Dice_Game.DB.model.domain.Role;
import Tasca.S5.__Dice_Game.DB.model.dto.PlayerDTO;

import java.util.List;

public interface PlayerService {

    PlayerDTO createPlayer(PlayerDTO playerDTO);

    PlayerDTO updatePlayerName(String id, PlayerDTO playerDTO);

    List<PlayerDTO> getAllPlayers();

    PlayerDTO getPlayerById(String id);

    String getAverageSuccessRate();

    long totalPlayedGames();

    PlayerDTO getPlayerWithLowestSuccessRate();

    PlayerDTO getPlayerWithHighestSuccessRate();


}
