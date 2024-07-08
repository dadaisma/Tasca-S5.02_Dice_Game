package Tasca.S5.__Dice_Game.DB.model.service;

import Tasca.S5.__Dice_Game.DB.model.dto.GameDTO;
import Tasca.S5.__Dice_Game.DB.model.domain.Game;
import Tasca.S5.__Dice_Game.DB.model.domain.Player;
import Tasca.S5.__Dice_Game.DB.model.repository.GameRepository;
import Tasca.S5.__Dice_Game.DB.model.repository.PlayerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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

    private List<Game> getGamesForPlayer(String playerId) {
        Optional<Player> playerOpt = playerRepository.findById(playerId);
        if (playerOpt.isPresent()) {
            Optional<List<Game>> gamesOpt = gameRepository.findByPlayerId(playerId);
            if (gamesOpt.isPresent()) {
                return gamesOpt.get();
            } else {
                throw new EntityNotFoundException("Games for Player with ID " + playerId + " not found in MySQL");
            }
        } else {
            throw new EntityNotFoundException("Player with ID " + playerId + " not found in MongoDB");
        }
    }


    @Override
    public GameDTO createGame(String playerId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserId = ((Player) authentication.getPrincipal()).getId();
        boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));


        if (!isAdmin && !playerId.equals(currentUserId)) {
            throw new InsufficientAuthenticationException("You don't have permissions to modify this player's data");
        }

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
        List<Game> games = getGamesForPlayer(playerId);

        return games.stream()
                .map(game -> new GameDTO(game.getId(), game.getDie1(), game.getDie2(), game.isWon()))
                .collect(Collectors.toList());
    }

    @Override
    public List<GameDTO> getAllGameDtos() {
        return gameRepository.findAll().stream()
                .map(game -> new GameDTO(game.getId(), game.getDie1(), game.getDie2(), game.isWon()))
                .collect(Collectors.toList());
    }


    @Override
    public void deletePlayerGames(String playerId) {
        List<Game> games = getGamesForPlayer(playerId);
        for (Game game : games) {
            gameRepository.delete(game);
        }
    }



}
