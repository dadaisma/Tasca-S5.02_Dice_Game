package Tasca.S5.__Dice_Game.DB.model.service;

import Tasca.S5.__Dice_Game.DB.model.domain.Game;
import Tasca.S5.__Dice_Game.DB.model.domain.Player;
import Tasca.S5.__Dice_Game.DB.model.dto.PlayerDTO;
import Tasca.S5.__Dice_Game.DB.model.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlayerServiceImp implements PlayerService {

    @Autowired
    private PlayerRepository playerRepository;

    @Override
    public PlayerDTO createPlayer(PlayerDTO playerDTO) {
        Player player = new Player();
        player.setName(playerDTO.getName() != null && !playerDTO.getName().isEmpty() ? playerDTO.getName() : "ANÒNIM");

        try {
            Player savedPlayer = playerRepository.save(player);
            return new PlayerDTO(savedPlayer.getId(), savedPlayer.getName(), savedPlayer.getRegistrationDate(), calculateSuccessRate(savedPlayer));
        } catch (DataIntegrityViolationException e) {
            // Handle unique constraint violation (duplicate name)
            e.printStackTrace(); // Log the exception
            throw new IllegalArgumentException("Player name must be unique");
        } catch (Exception e) {
            // Handle other exceptions
            e.printStackTrace(); // Log the exception
            throw new RuntimeException("Failed to create player: " + e.getMessage());
        }
    }

    @Override
    public PlayerDTO updatePlayerName(Long id, PlayerDTO playerDTO) {
        Optional<Player> playerOpt = playerRepository.findById(id);
        if (playerOpt.isPresent()) {
            Player player = playerOpt.get();
            player.setName(playerDTO.getName());

            Player updatedPlayer = playerRepository.save(player);
            return new PlayerDTO(updatedPlayer.getId(), updatedPlayer.getName(), updatedPlayer.getRegistrationDate(), calculateSuccessRate(updatedPlayer));
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
    public PlayerDTO getPlayerById(Long id) {
        Optional<Player> playerOpt = playerRepository.findById(id);
        return playerOpt.map(player -> new PlayerDTO(player.getId(), player.getName(), player.getRegistrationDate(), calculateSuccessRate(player))).orElse(null);
    }

    @Override
    public List<PlayerDTO> getAllPlayers() {
        return playerRepository.findAll().stream()
                .map(player -> new PlayerDTO(player.getId(), player.getName(), player.getRegistrationDate(), calculateSuccessRate(player)))
                .collect(Collectors.toList());
    }

    private double calculateSuccessRate(Player player) {
        List<Game> games = player.getGames() != null ? player.getGames() : Collections.emptyList();

        long totalGames = player.getGames().size();
        long wonGames = player.getGames().stream().filter(Game::isWon).count();
        return totalGames == 0 ? 0 : (double) wonGames / totalGames * 100;
    }

    @Override
    public double getAverageSuccessRate() {
        List<Player> players = playerRepository.findAll();
        if (players.isEmpty()) {
            return 0.0;
        }

        double totalSuccessRate = players.stream()
                .mapToDouble(this::calculateSuccessRate)
                .sum();

        return totalSuccessRate / players.size();
    }

    @Override
    public PlayerDTO getPlayerWithLowestSuccessRate() {
        List<Player> players = playerRepository.findAll();
        if (players.isEmpty()) {
            return null;
        }

        Player playerWithLowestSuccessRate = players.stream()
                .min(Comparator.comparingDouble(this::calculateSuccessRate))
                .orElse(null);

        return convertToPlayerDTO(playerWithLowestSuccessRate);
    }

    @Override
    public PlayerDTO getPlayerWithHighestSuccessRate() {
        List<Player> players = playerRepository.findAll();
        if (players.isEmpty()) {
            return null;
        }

        Player playerWithHighestSuccessRate = players.stream()
                .max(Comparator.comparingDouble(this::calculateSuccessRate))
                .orElse(null);

        return convertToPlayerDTO(playerWithHighestSuccessRate);
    }

    private PlayerDTO convertToPlayerDTO(Player player) {
        if (player == null) {
            return null;
        }
        return new PlayerDTO(player.getId(), player.getName(), player.getRegistrationDate(), calculateSuccessRate(player));
    }
}
