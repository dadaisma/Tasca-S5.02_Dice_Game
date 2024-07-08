package Tasca.S5.__Dice_Game.DB.model.dto;

import Tasca.S5.__Dice_Game.DB.model.domain.Player;
import Tasca.S5.__Dice_Game.DB.model.domain.Role;
import lombok.*;

import java.text.DecimalFormat;
import java.time.LocalDate;

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
    private Role role;

    private String successRate;
    @Setter
    private long totalPlayedGames;

    public PlayerDTO(String name, String email, String password) {
        this.name = (name != null && !name.isEmpty()) ? name : "ANÒNIM";
        this.registrationDate = LocalDate.now();
        this.email = email;
        this.password = password;
        //this.role = Role.USER;
    }

    public PlayerDTO(Player player) {
        this.id = player.getId();
        this.name = player.getName();
        this.registrationDate = player.getRegistrationDate();
        this.email = player.getEmail();
        this.password = player.getPassword();
       this.role = player.getRole();

    }
    public void setSuccessRate(double successRate) {
        DecimalFormat df = new DecimalFormat("0.00");
        this.successRate = df.format(successRate) + " %";
    }


}
