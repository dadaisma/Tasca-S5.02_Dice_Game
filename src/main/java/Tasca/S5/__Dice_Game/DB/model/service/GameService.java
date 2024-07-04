package Tasca.S5.__Dice_Game.DB.model.service;


import Tasca.S5.__Dice_Game.DB.model.dto.GameDTO;


import java.util.List;


public interface GameService {

    GameDTO createGame(String playerId);

    List<GameDTO> getGamesByPlayerId(String playerId);

   void deletePlayerGames(String playerId);

    List<GameDTO> getAllGameDtos();


   // public void updatePlayerGamesInMongoDB(String playerId);



}
