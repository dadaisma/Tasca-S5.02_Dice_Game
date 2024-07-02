package Tasca.S5.__Dice_Game.DB.model.service;

import Tasca.S5.__Dice_Game.DB.model.domain.Game;
import Tasca.S5.__Dice_Game.DB.model.domain.Player;
import Tasca.S5.__Dice_Game.DB.model.dto.PlayerDTO;
import Tasca.S5.__Dice_Game.DB.model.repository.PlayerRepository;
import Tasca.S5.__Dice_Game.DB.model.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        player.setRegistrationDate(playerDTO.getRegistrationDate());

        Player savedPlayer = playerRepository.save(player);
        return new PlayerDTO(savedPlayer.getId(), savedPlayer.getName(), savedPlayer.getRegistrationDate(), calculateSuccessRate(savedPlayer));
    }

    @Override
    public PlayerDTO updatePlayerName(Long id, String name) {
        Optional<Player> playerOpt = playerRepository.findById(id);
        if (playerOpt.isPresent()) {
            Player player = playerOpt.get();
            player.setName(name);
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
        long totalGames = player.getGames().size();
        long wonGames = player.getGames().stream().filter(Game::isWon).count();
        return totalGames == 0 ? 0 : (double) wonGames / totalGames * 100;
    }
}
