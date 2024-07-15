package Tasca.S5.__Dice_Game.DB.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class CustomPlayerDTO {
    private String id;
    private String name;
    private String successRate;
    private long totalPlayedGames;
}