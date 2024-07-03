package Tasca.S5.__Dice_Game.DB.model.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


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

    private String email;
    private String password;


    private List<Game> games = new ArrayList<>();

    public Player(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.registrationDate = LocalDate.now();
    }



}
