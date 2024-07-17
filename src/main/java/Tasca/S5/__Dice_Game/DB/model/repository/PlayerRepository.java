package Tasca.S5.__Dice_Game.DB.model.repository;


import Tasca.S5.__Dice_Game.DB.model.domain.Player;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerRepository extends MongoRepository<Player, String> {
    Optional<Player> findByName(String name);
    Optional<Player> findByEmail(String email);
    Optional<Object> findByNameIgnoreCaseAndIdNot(String name, String currentUserId);
    Optional<Object> findByEmailIgnoreCaseAndIdNot(String email, String currentUserId);
}