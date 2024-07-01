package Tasca.S5.__Dice_Game.DB.model.service;


import Tasca.S5.__Dice_Game.DB.model.domain.Game;
import Tasca.S5.__Dice_Game.DB.model.domain.Player;
import Tasca.S5.__Dice_Game.DB.model.repository.GameRepository;
import Tasca.S5.__Dice_Game.DB.model.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class GameServiceImp implements GameService {


    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PlayerRepository playerRepository;

    private final Random random = new Random();

    public Game createGame(Long playerId) {
        Optional<Player> playerOpt = playerRepository.findById(playerId);
        if (playerOpt.isPresent()) {
            Player player = playerOpt.get();
            Game game = new Game();
            game.setDie1(random.nextInt(6) + 1);
            game.setDie2(random.nextInt(6) + 1);
            game.setWon(game.getDie1() + game.getDie2() == 7);
            game.setPlayer(player);
            return gameRepository.save(game);
        }
        return null;
    }

    public List<Game> getGamesByPlayerId(Long playerId) {
        Optional<Player> playerOpt = playerRepository.findById(playerId);
        return playerOpt.map(Player::getGames).orElse(null);
    }
}
