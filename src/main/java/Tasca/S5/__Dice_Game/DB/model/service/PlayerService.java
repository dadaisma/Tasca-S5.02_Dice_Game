package Tasca.S5.__Dice_Game.DB.model.service;

import Tasca.S5.__Dice_Game.DB.model.dto.PlayerDTO;

import java.util.List;

public interface PlayerService {

    PlayerDTO createPlayer(PlayerDTO playerDTO);

    PlayerDTO updatePlayerName(Long id, String name);

    void deletePlayerGames(Long playerId);

    PlayerDTO getPlayerById(Long id);

    List<PlayerDTO> getAllPlayers();
}
