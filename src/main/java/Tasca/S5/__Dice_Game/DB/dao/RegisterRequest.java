package Tasca.S5.__Dice_Game.DB.dao;

import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor


public class RegisterRequest {
    @NonNull
    String email;
    @NonNull
    String password;
    String name;
    private LocalDate registrationDate;
}
