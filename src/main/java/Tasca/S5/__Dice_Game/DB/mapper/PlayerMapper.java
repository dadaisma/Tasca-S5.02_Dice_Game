package Tasca.S5.__Dice_Game.DB.mapper;

import Tasca.S5.__Dice_Game.DB.model.domain.Player;
import Tasca.S5.__Dice_Game.DB.model.dto.CustomPlayerDTO;
import Tasca.S5.__Dice_Game.DB.model.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlayerMapper {

    @Autowired
    private GameRepository gameRepository;

    public CustomPlayerDTO toCustomPlayerDTO(Player player) {
        if (player == null) {
            return null;
        }

        return CustomPlayerDTO.builder()
                .id(player.getId())
                .name(player.getName())
                .successRate(calculateSuccessRate(player.getId()))
                .totalPlayedGames(gameRepository.countByPlayerId(player.getId()))
                .build();
    }

    private String calculateSuccessRate(String playerId) {
        long totalGames = gameRepository.countByPlayerId(playerId);
        long wonGames = gameRepository.countByPlayerIdAndWon(playerId, true);
        double successRate = totalGames == 0 ? 0 : (double) wonGames / totalGames * 100;
        return String.format("%.2f %%", successRate);
    }
}