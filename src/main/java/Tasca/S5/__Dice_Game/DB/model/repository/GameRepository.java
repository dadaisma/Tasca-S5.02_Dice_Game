package Tasca.S5.__Dice_Game.DB.model.repository;

import Tasca.S5.__Dice_Game.DB.model.domain.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
}