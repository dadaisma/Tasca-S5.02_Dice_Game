package Tasca.S5.__Dice_Game.DB.model.service;

import Tasca.S5.__Dice_Game.DB.dao.LoginRequest;
import Tasca.S5.__Dice_Game.DB.dao.RegisterRequest;
import Tasca.S5.__Dice_Game.DB.dao.response.JwtAuthenticationResponse;
import Tasca.S5.__Dice_Game.DB.model.domain.Player;
import Tasca.S5.__Dice_Game.DB.model.domain.Role;
import Tasca.S5.__Dice_Game.DB.model.repository.PlayerRepository;
import Tasca.S5.__Dice_Game.DB.security.JwtService;
import Tasca.S5.__Dice_Game.DB.utils.HeaderUtil;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PlayerRepository playerRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;



    public JwtAuthenticationResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            UserDetails user = playerRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + request.getEmail()));

            String token = jwtService.getToken(user);
            HeaderUtil.setToken(token);

            return JwtAuthenticationResponse.builder()
                    .token(token)
                    .expiresIn(jwtService.getExpirationTime())
                    .build();
        } catch (AuthenticationException e) {
            throw new IllegalArgumentException("Invalid email or password");
        }
    }

    public JwtAuthenticationResponse signUp(RegisterRequest request) {
        if (StringUtils.isEmpty(request.getEmail())) {
            throw new IllegalArgumentException("Email cannot be empty");
        }


        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(request.getEmail());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }

        if (StringUtils.isEmpty(request.getPassword())) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (playerRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EntityExistsException("Player with email " + request.getEmail() + " already exists.");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        Role role = Role.ROLE_USER; // Default role


        Player user = Player.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .name(request.getName() != null && !request.getName().isEmpty() ? request.getName() : "ANÒNIM")
                .registrationDate(request.getRegistrationDate() != null ? request.getRegistrationDate() : LocalDate.now())
                .role(role)
                .build();

        playerRepository.save(user);


        String token = jwtService.getToken(user);
        HttpHeaders headers = HeaderUtil.createHeaders(token);
        HeaderUtil.setToken(token);

        return JwtAuthenticationResponse.builder()
                .token(token)
                .expiresIn(jwtService.getExpirationTime())
                .build();
    }

    public ResponseEntity<String> logout(String authorizationHeader) {
        try {
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return new ResponseEntity<>("Invalid Authorization header.", HttpStatus.BAD_REQUEST);
            }

            String jwt = extractJwtFromAuthorizationHeader(authorizationHeader);
            if (!jwtService.validateToken(jwt)) {
                return new ResponseEntity<>("Invalid or expired token.", HttpStatus.UNAUTHORIZED);
            }

            jwtService.invalidateToken(jwt);
            return new ResponseEntity<>("Logged out successfully.", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Error logging out: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String extractJwtFromAuthorizationHeader(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        throw new IllegalArgumentException("Invalid Authorization header format.");
    }
}