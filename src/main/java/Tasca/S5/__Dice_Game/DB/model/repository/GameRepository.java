package Tasca.S5.__Dice_Game.DB.model.repository;

import Tasca.S5.__Dice_Game.DB.model.domain.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, String> {
    Optional<List<Game>> findByPlayerId(String playerId);
    long countByPlayerId(String playerId);
    long countByPlayerIdAndWon(String playerId, boolean won);
}