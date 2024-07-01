package Tasca.S5.__Dice_Game.DB.model.repository;


import Tasca.S5.__Dice_Game.DB.model.domain.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
}