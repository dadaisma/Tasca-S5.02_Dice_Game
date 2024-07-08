package Tasca.S5.__Dice_Game.DB.repository;

import Tasca.S5.__Dice_Game.DB.model.domain.Game;
import Tasca.S5.__Dice_Game.DB.model.repository.GameRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application.properties")
public class GameRepositoryTest {

    @Autowired
    private GameRepository gameRepository;

    @Test
    public void testFindByPlayerId() {
        // Given
        String playerId = "player1";
        Game game1 = new Game(null, 4, 3, true, playerId);
        Game game2 = new Game(null, 5, 3, false, playerId);
        gameRepository.save(game1);
        gameRepository.save(game2);

        // When
        List<Game> games = gameRepository.findByPlayerId(playerId).orElseThrow();

        // Then
        assertEquals(2, games.size());
        assertEquals(playerId, games.get(0).getPlayerId());
    }
    @Test
    public void testCountByPlayerId() {
        // Given
        String playerId = "player2";
        Game game1 = new Game(null, 6, 1, true, playerId);
        Game game2 = new Game(null, 2, 6, false, playerId);
        gameRepository.save(game1);
        gameRepository.save(game2);

        // When
        long count = gameRepository.countByPlayerId(playerId);

        // Then
        assertEquals(2, count);
    }

    @Test
    public void testCountByPlayerIdAndWon() {
        // Given
        String playerId = "player3";
        Game game1 = new Game(null, 5, 2, true, playerId);
        Game game2 = new Game(null, 4, 3, true, playerId);
        Game game3 = new Game(null, 2, 6, false, playerId);
        gameRepository.save(game1);
        gameRepository.save(game2);
        gameRepository.save(game3);

        // When
        long countWon = gameRepository.countByPlayerIdAndWon(playerId, true);

        // Then
        assertEquals(2, countWon);
    }

    @Test
    public void testCountByPlayerIdAndNotWon() {
        // Given
        String playerId = "player4";
        Game game1 = new Game(null, 4, 3, true, playerId);
        Game game2 = new Game(null, 5, 3, false, playerId);
        Game game3 = new Game(null, 2, 1, false, playerId);
        gameRepository.save(game1);
        gameRepository.save(game2);
        gameRepository.save(game3);

        // When
        long countNotWon = gameRepository.countByPlayerIdAndWon(playerId, false);

        // Then
        assertEquals(2, countNotWon);
    }

    @Test
    public void testCountByNonExistingPlayerId() {
        // Given
        String playerId = "player5"; // Assuming player5 does not exist in the database

        // When
        long count = gameRepository.countByPlayerId(playerId);

        // Then
        assertEquals(0, count);
    }
}
