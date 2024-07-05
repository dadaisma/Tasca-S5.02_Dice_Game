package Tasca.S5.__Dice_Game.DB.model.service;

import Tasca.S5.__Dice_Game.DB.dao.LoginRequest;
import Tasca.S5.__Dice_Game.DB.dao.RegisterRequest;
import Tasca.S5.__Dice_Game.DB.dao.response.JwtAuthenticationResponse;
import Tasca.S5.__Dice_Game.DB.model.domain.Player;
import Tasca.S5.__Dice_Game.DB.model.domain.Role;
import Tasca.S5.__Dice_Game.DB.model.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PlayerRepository playerRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;



    public JwtAuthenticationResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword()));
        UserDetails user = playerRepository.findByEmail(request.getEmail()).orElseThrow();
        String token= jwtService.getToken(user);
        return JwtAuthenticationResponse.builder()
                .token(token)
                .build();
    }

    public JwtAuthenticationResponse signUp(RegisterRequest request) {
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        Player user = Player.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .name(request.getName())
                .registrationDate(request.getRegistrationDate() != null ? request.getRegistrationDate() : LocalDate.now())
                .role(Role.USER)
                .build();

        playerRepository.save(user);


        String token = jwtService.getToken(user);

        return JwtAuthenticationResponse.builder()
                .token(token)
                .build();
    }



}
