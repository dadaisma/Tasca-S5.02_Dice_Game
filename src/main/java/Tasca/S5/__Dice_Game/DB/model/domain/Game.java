package Tasca.S5.__Dice_Game.DB.model.domain;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int die1;
    private int die2;
    private boolean won;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;


}
