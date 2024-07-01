package Tasca.S5.__Dice_Game.DB.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class GameDTO {
    private Long id;
    private int die1;
    private int die2;
    private boolean won;

}
