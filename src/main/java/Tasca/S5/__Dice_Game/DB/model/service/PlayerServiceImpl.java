package Tasca.S5.__Dice_Game.DB.model.service;

import Tasca.S5.__Dice_Game.DB.model.domain.Game;
import Tasca.S5.__Dice_Game.DB.model.domain.Player;
import Tasca.S5.__Dice_Game.DB.model.dto.PlayerDTO;
import Tasca.S5.__Dice_Game.DB.model.repository.GameRepository;
import Tasca.S5.__Dice_Game.DB.model.repository.PlayerRepository;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerServiceImpl(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Autowired
    private GameRepository gameRepository;

    @Override
    public PlayerDTO createPlayer(PlayerDTO playerDTO) {
        // Create a new Player entity from the provided PlayerDTO
        Player player = new Player(playerDTO.getName(), playerDTO.getEmail(), playerDTO.getPassword());

        // Set default name if the provided name is empty
        if (player.getName() == null || player.getName().isEmpty()) {
            player.setName("ANÒNIM");
        }

        // Check if a player with the same name already exists (excluding "ANÒNIM")
        if (!player.getName().equals("ANÒNIM") && playerRepository.findByName(player.getName()).isPresent()) {
            throw new EntityExistsException("Create new Player Failed: Invalid Player name: " + player.getName() + " -> ALREADY EXISTS in DataBase");
        }

        if (StringUtils.isEmpty(playerDTO.getEmail())) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (StringUtils.isEmpty(playerDTO.getPassword())) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        // Check if a player with the same email already exists
        if (playerRepository.findByEmail(player.getEmail()).isPresent()) {
            throw new EntityExistsException("Create new Player Failed: Invalid email: " + player.getEmail() + " -> ALREADY EXISTS in DataBase");
        }

        try {
            // Save the new player to the repository
            Player savedPlayer = playerRepository.save(player);

            // Count total played games in MySQL for the saved player
            long totalPlayedGames = gameRepository.countByPlayerId(savedPlayer.getId());

            // Create and return a PlayerDTO from the saved Player entity and total played games
            return new PlayerDTO(savedPlayer, totalPlayedGames);

        } catch (Exception e) {
            // Handle any exceptions that occur during the save operation
            throw new RuntimeException("Failed to create player: " + e.getMessage(), e);
        }
    }

        @Override
        public PlayerDTO updatePlayerName(String id, PlayerDTO playerDTO) {
            Player player = playerRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Update Player Failed: Invalid player id: " + id + " -> DOESN'T EXIST in DataBase"));

            if (StringUtils.isEmpty(playerDTO.getName())) {
                throw new IllegalArgumentException("Name cannot be empty");
            }
            if (StringUtils.isEmpty(playerDTO.getEmail())) {
                throw new IllegalArgumentException("Email cannot be empty");
            }
            if (StringUtils.isEmpty(playerDTO.getPassword())) {
                throw new IllegalArgumentException("Password cannot be empty");
            }

            player.setName(playerDTO.getName());
            player.setEmail(playerDTO.getEmail());
            player.setPassword(playerDTO.getPassword());
            Player updatedPlayer = playerRepository.save(player);

            long totalPlayedGames = gameRepository.countByPlayerId(updatedPlayer.getId());
            return new PlayerDTO(updatedPlayer, totalPlayedGames);
        }


        @Override
        public PlayerDTO getPlayerById(String id) {
            Player player = playerRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Get One Player Failed: Invalid player id: " + id + " -> DOESN'T EXIST in DataBase"));

            long totalPlayedGames = gameRepository.countByPlayerId(player.getId());
            return new PlayerDTO(player, totalPlayedGames);
        }

    @Override
    public List<PlayerDTO> getAllPlayers() {
        return playerRepository.findAll().stream()
                .map(player -> {
                    long totalPlayedGames = gameRepository.countByPlayerId(player.getId()); // Count games in MySQL
                    return new PlayerDTO(player, totalPlayedGames);
                })
                .collect(Collectors.toList());
    }

    private List<Game> getGamesForPlayer(String playerId) {
        Optional<List<Game>> gamesOpt = gameRepository.findByPlayerId(playerId);
        if (gamesOpt.isPresent()) {
            return gamesOpt.get();
        } else {
            return Collections.emptyList();
        }
    }
/*
    @Override
    public String getAverageSuccessRate() {
        List<Player> players = playerRepository.findAll();
        if (players.isEmpty()) {
            return "No players found.";
        }

        double totalSuccessRate = players.stream()
                .mapToDouble(player -> new PlayerDTO(player).getSuccessRate())
                .sum();

        double averageSuccessRate = totalSuccessRate / players.size();
        long totalGamesPlayed = players.stream()
                .mapToLong(player -> new PlayerDTO(player).getTotalPlayedGames())
                .sum();

        return String.format("The success rate is %.2f %% on an overall of games played of %d", averageSuccessRate, totalGamesPlayed);
    }

    @Override
    public PlayerDTO getPlayerWithLowestSuccessRate() {
        return playerRepository.findAll().stream()
                .min(Comparator.comparingDouble(player -> new PlayerDTO(player).getSuccessRate()))
                .map(PlayerDTO::new)
                .orElse(null);
    }

    @Override
    public PlayerDTO getPlayerWithHighestSuccessRate() {
        return playerRepository.findAll().stream()
                .max(Comparator.comparingDouble(player -> new PlayerDTO(player).getSuccessRate()))
                .map(PlayerDTO::new)
                .orElse(null);
    }

 */
}
