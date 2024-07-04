package Tasca.S5.__Dice_Game.DB.model.service;

import Tasca.S5.__Dice_Game.DB.model.dto.GameDTO;
import Tasca.S5.__Dice_Game.DB.model.domain.Game;
import Tasca.S5.__Dice_Game.DB.model.domain.Player;
import Tasca.S5.__Dice_Game.DB.model.repository.GameRepository;
import Tasca.S5.__Dice_Game.DB.model.repository.PlayerRepository;
import jakarta.persistence.EntityNotFoundException;
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
    public GameDTO createGame(String playerId) {
        if (playerId == null) {
            throw new IllegalArgumentException("Player ID cannot be null");
        }

        Optional<Player> playerOpt = playerRepository.findById(playerId);

        if (playerOpt.isEmpty()) {
            throw new EntityNotFoundException("Create Game Failed: Player with ID " + playerId + " not found in database");
        }

        Player player = playerOpt.get();
        Game game = new Game();
        game.setDie1(random.nextInt(6) + 1);
        game.setDie2(random.nextInt(6) + 1);
        game.setWon(game.getDie1() + game.getDie2() == 7);
        game.setPlayerId(player.getId());
        Game savedGame = gameRepository.save(game);
        return new GameDTO(savedGame.getId(), savedGame.getDie1(), savedGame.getDie2(), savedGame.isWon());
    }


    @Override
    public List<GameDTO> getGamesByPlayerId(String playerId) {
        Optional<Player> playerOpt = playerRepository.findById(playerId);
        if (playerOpt.isPresent()) {
            // Fetch games from MySQL using playerId
            Optional<List<Game>> gamesOpt = gameRepository.findByPlayerId(playerId);
            if (gamesOpt.isPresent()) {
                return gamesOpt.get().stream()
                        .map(game -> new GameDTO(game.getId(), game.getDie1(), game.getDie2(), game.isWon()))
                        .collect(Collectors.toList());
            } else {
                throw new EntityNotFoundException("Games for Player with ID " + playerId + " not found in MySQL");
            }
        }
        throw new EntityNotFoundException("Player with ID " + playerId + " not found in MongoDB");
    }


    @Override
    public List<GameDTO> getAllGameDtos() {
        return gameRepository.findAll().stream()
                .map(game -> new GameDTO(game.getId(), game.getDie1(), game.getDie2(), game.isWon()))
                .collect(Collectors.toList());
    }


    @Override
    public void deletePlayerGames(String playerId) {
        // Fetch the player from MongoDB
        Optional<Player> playerOpt = playerRepository.findById(playerId);
        if (playerOpt.isPresent()) {
            // Fetch games from MySQL using playerId
            Optional<List<Game>> gamesOpt = gameRepository.findByPlayerId(playerId);
            if (gamesOpt.isPresent()) {
                List<Game> games = gamesOpt.get();

                // Delete each game from MySQL
                for (Game game : games) {
                    gameRepository.delete(game);
                }
            } else {
                throw new EntityNotFoundException("No games found for Player with ID " + playerId + " in MySQL");
            }
        } else {
            throw new EntityNotFoundException("Player with ID " + playerId + " not found in MongoDB");
        }
    }

}
