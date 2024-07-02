package Tasca.S5.__Dice_Game.DB.model.service;

import Tasca.S5.__Dice_Game.DB.model.domain.Game;
import Tasca.S5.__Dice_Game.DB.model.domain.Player;
import Tasca.S5.__Dice_Game.DB.model.dto.PlayerDTO;
import Tasca.S5.__Dice_Game.DB.model.repository.PlayerRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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


    @Override
    public PlayerDTO createPlayer(PlayerDTO playerDTO) {

        Player player = new Player(playerDTO.getName());

        if (!player.getName().equals("ANÒNIM") && playerRepository.findByName(player.getName()).isPresent()) {
            throw new EntityExistsException("Create new Player Failed: Invalid Player name: " + player.getName() + " -> ALREADY EXISTS in DataBase");
        }

        try {
            Player savedPlayer = playerRepository.save(player);
            return new PlayerDTO(savedPlayer);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to create player: " + e.getMessage());
        }
    }
    @Override
    public PlayerDTO updatePlayerName(Long id, PlayerDTO playerDTO) {
        Optional<Player> playerOpt = playerRepository.findById(id);
        if (playerOpt.isEmpty()) {
            throw new EntityNotFoundException("Update Player Failed: Invalid player id: " + id +
                    " -> DOESN'T EXIST in DataBase");
        }

        Player player = playerOpt.get();
        player.setName(playerDTO.getName());

        Player updatedPlayer = playerRepository.save(player);
        return new PlayerDTO(updatedPlayer);
    }

    @Override
    public void deletePlayerGames(Long playerId) {
        if(!playerRepository.findById(playerId).isPresent()){
            throw new EntityNotFoundException("Delete Player Failed: Invalid player id: "+ playerId+
                    " -> DOESN'T EXIST in DataBase");
        }
        Optional<Player> playerOpt = playerRepository.findById(playerId);
        if (playerOpt.isPresent()) {
            Player player = playerOpt.get();
            player.getGames().clear();
            playerRepository.save(player);
        }
    }

    @Override
    public PlayerDTO getPlayerById(Long id) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Get One Player Failed: Invalid player id: " + id +
                        " -> DOESN'T EXIST in DataBase"));

        return new PlayerDTO(player);
    }

    @Override
    public List<PlayerDTO> getAllPlayers() {
        return playerRepository.findAll().stream()
                .map(PlayerDTO::new)
                .collect(Collectors.toList());
    }



    @Override
    public double getAverageSuccessRate() {
        List<Player> players = playerRepository.findAll();
        if (players.isEmpty()) {
            return 0.0;
        }

        double totalSuccessRate = players.stream()
                .mapToDouble(player -> new PlayerDTO(player).getSuccessRate())
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
                .min(Comparator.comparingDouble(player -> new PlayerDTO(player).getSuccessRate()))
                .orElse(null);

        return new PlayerDTO(playerWithLowestSuccessRate);
    }

    @Override
    public PlayerDTO getPlayerWithHighestSuccessRate() {
        List<Player> players = playerRepository.findAll();
        if (players.isEmpty()) {
            return null;
        }

        Player playerWithHighestSuccessRate = players.stream()
                .max(Comparator.comparingDouble(player -> new PlayerDTO(player).getSuccessRate()))
                .orElse(null);

        return new PlayerDTO(playerWithHighestSuccessRate);
    }

}
