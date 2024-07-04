package Tasca.S5.__Dice_Game.DB.model.dto;

import Tasca.S5.__Dice_Game.DB.model.domain.Game;
import Tasca.S5.__Dice_Game.DB.model.domain.Player;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data

public class PlayerDTO {

    private String id;

    private String name;
    private String email;
    private String password;
    private LocalDate registrationDate;
    @JsonIgnore
    private double successRate;

    private long totalPlayedGames;
 //   private long totalPlayedGamesOverall;


    public PlayerDTO(String name, String email, String password) {

        this.name = (name != null && !name.isEmpty()) ? name : "ANÒNIM";
        this.successRate = 0.0;
        this.email = email;
        this.password = password;

 //       this.totalPlayedGamesOverall = 0;
    }

    public PlayerDTO(Player player) {
        this.id = player.getId();

        this.name = player.getName();
        this.registrationDate = player.getRegistrationDate();
        /*
        this.successRate = roundToTwoDecimalPlaces(calculateSuccessRate(player)) ;
        this.totalPlayedGames = player.getGames().size();

         */
        this.email = player.getEmail();
        this.password = player.getPassword();

       // this.totalPlayedGamesOverall = getTotalPlayedGames(player);
    }
/*
    private double calculateSuccessRate(Player player) {
        List<Game> games = player.getGames() != null ? player.getGames() : Collections.emptyList();

        long totalGames = player.getGames().size();
        long wonGames = player.getGames().stream().filter(Game::isWon).count();
        return totalGames == 0 ? 0 : (double) wonGames / totalGames * 100;
    }
    private long getTotalPlayedGames(Player player) {
        List<Game> games = player.getGames() != null ? player.getGames() : Collections.emptyList();
        return games.size();
    }

    private double roundToTwoDecimalPlaces(double value) {
        return Double.parseDouble(String.format(Locale.US, "%.2f ", value )) ;
    }

    public String getSuccess_Rate() {
        return String.format(Locale.US, "%.2f %%", successRate);
    }

 */
}