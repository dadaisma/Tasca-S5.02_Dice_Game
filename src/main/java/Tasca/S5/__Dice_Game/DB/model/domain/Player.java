package Tasca.S5.__Dice_Game.DB.model.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;



@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

@Document(collection = "player")
public class Player {

    @Id
    private String id;
    private String name;
    private LocalDate registrationDate;
    private Role role;
    private String email;
    private String password;


   // private List<Game> games = new ArrayList<>();

    public Player(String name, String email, String password, Role Role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.registrationDate = LocalDate.now();
        this.role = Role.USER;
    }



}
