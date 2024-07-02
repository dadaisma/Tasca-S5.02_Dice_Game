package Tasca.S5.__Dice_Game.DB.model.dto;

import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data

public class PlayerDTO {

    private Long id;
    private String name;
    private LocalDate registrationDate;
    private double successRate;


}