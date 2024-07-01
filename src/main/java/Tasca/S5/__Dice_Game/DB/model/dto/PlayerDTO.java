package Tasca.S5.__Dice_Game.DB.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class PlayerDTO {

    private Long id;
    private String name;
    private LocalDate registrationDate;
    private double successRate;


}