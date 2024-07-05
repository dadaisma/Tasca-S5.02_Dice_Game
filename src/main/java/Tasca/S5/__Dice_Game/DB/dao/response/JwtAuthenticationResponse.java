package Tasca.S5.__Dice_Game.DB.dao.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class JwtAuthenticationResponse {
    private String token;

    private long expiresIn;
}
