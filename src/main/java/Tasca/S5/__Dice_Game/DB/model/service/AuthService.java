package Tasca.S5.__Dice_Game.DB.model.service;

import Tasca.S5.__Dice_Game.DB.dao.LoginRequest;
import Tasca.S5.__Dice_Game.DB.dao.RegisterRequest;
import Tasca.S5.__Dice_Game.DB.dao.response.JwtAuthenticationResponse;
import Tasca.S5.__Dice_Game.DB.model.domain.Player;
import Tasca.S5.__Dice_Game.DB.model.domain.Role;
import Tasca.S5.__Dice_Game.DB.model.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PlayerRepository playerRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public JwtAuthenticationResponse login(LoginRequest request) {
        return null;
    }

    public JwtAuthenticationResponse signUp(RegisterRequest request) {
        Player user = Player.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .name(request.getName())
                .role(Role.USER)
                .build();

        playerRepository.save(user);


        return JwtAuthenticationResponse.builder()
                .token(jwtService.getToken(user))
                .build();
    }



}
