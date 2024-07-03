package Tasca.S5.__Dice_Game.DB.model.service;

import Tasca.S5.__Dice_Game.DB.model.dto.PlayerDTO;

import java.util.List;

public interface PlayerService {

    PlayerDTO createPlayer(PlayerDTO playerDTO);

    PlayerDTO updatePlayerName(String id, PlayerDTO playerDTO);

    void deletePlayerGames(String playerId);

    PlayerDTO getPlayerById(String id);

    List<PlayerDTO> getAllPlayers();

   // double getAverageSuccessRate();

    PlayerDTO getPlayerWithLowestSuccessRate();

    PlayerDTO getPlayerWithHighestSuccessRate();

    String getAverageSuccessRate();
}
