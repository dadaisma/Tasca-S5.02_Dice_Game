package Tasca.S5.__Dice_Game.DB.model.service;

import Tasca.S5.__Dice_Game.DB.model.dto.GameDTO;
import Tasca.S5.__Dice_Game.DB.model.domain.Game;
import Tasca.S5.__Dice_Game.DB.model.domain.Player;
import Tasca.S5.__Dice_Game.DB.model.repository.GameRepository;
import Tasca.S5.__Dice_Game.DB.model.repository.PlayerRepository;
import Tasca.S5.__Dice_Game.DB.model.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class GameServiceImp implements GameService {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PlayerRepository playerRepository;

    private final Random random = new Random();

    @Override
    public GameDTO createGame(Long playerId) {
        Optional<Player> playerOpt = playerRepository.findById(playerId);
        if (playerOpt.isPresent()) {
            Player player = playerOpt.get();
            Game game = new Game();
            game.setDie1(random.nextInt(6) + 1);
            game.setDie2(random.nextInt(6) + 1);
            game.setWon(game.getDie1() + game.getDie2() == 7);
            game.setPlayer(player);
            Game savedGame = gameRepository.save(game);
            return new GameDTO(savedGame.getId(), savedGame.getDie1(), savedGame.getDie2(), savedGame.isWon());
        }
        return null;
    }

    @Override
    public List<GameDTO> getGamesByPlayerId(Long playerId) {
        Optional<Player> playerOpt = playerRepository.findById(playerId);
        if (playerOpt.isPresent()) {
            return playerOpt.get().getGames().stream()
                    .map(game -> new GameDTO(game.getId(), game.getDie1(), game.getDie2(), game.isWon()))
                    .collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public void deletePlayerGames(Long playerId) {
        Optional<Player> playerOpt = playerRepository.findById(playerId);
        if (playerOpt.isPresent()) {
            Player player = playerOpt.get();
            player.getGames().clear();
            playerRepository.save(player);
        }
    }

    @Override
    public List<GameDTO> getAllGameDtos() {
        return gameRepository.findAll().stream()
                .map(game -> new GameDTO(game.getId(), game.getDie1(), game.getDie2(), game.isWon()))
                .collect(Collectors.toList());
    }
}
