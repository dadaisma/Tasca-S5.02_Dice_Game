package Tasca.S5.__Dice_Game.DB.controllers;

import Tasca.S5.__Dice_Game.DB.dao.LoginRequest;
import Tasca.S5.__Dice_Game.DB.dao.RegisterRequest;
import Tasca.S5.__Dice_Game.DB.dao.response.JwtAuthenticationResponse;
import Tasca.S5.__Dice_Game.DB.model.service.AuthService;
import Tasca.S5.__Dice_Game.DB.security.JwtService;
import Tasca.S5.__Dice_Game.DB.utils.HeaderUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor


public class AuthenticationController {

    private final AuthService authService;
    private final JwtService jwtService;

    @Operation(summary = "Authenticates a player and return a JWT token")
    @PostMapping(value = "login")
    public ResponseEntity<JwtAuthenticationResponse> login(@RequestBody LoginRequest request) {
        JwtAuthenticationResponse response = authService.login(request);
        HttpHeaders headers = HeaderUtil.createHeaders(response.getToken());

        return ResponseEntity.ok().headers(headers).body(response);
    }

    @Operation(summary = "Register a new player and return a JWT token")
    @PostMapping(value = "register")
    public ResponseEntity<JwtAuthenticationResponse> signUp(@RequestBody RegisterRequest request){
        try {
            JwtAuthenticationResponse response = authService.signUp(request);
            HttpHeaders headers = HeaderUtil.createHeaders(response.getToken());
            return ResponseEntity.ok().headers(headers).body(response);
        } catch (EntityExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new JwtAuthenticationResponse(e.getMessage(), 0));
        }
    }

    @Operation(summary = "Log out and invalidate the JWT token")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Extract the JWT token from the Authorization header
            String jwt = extractJwtFromAuthorizationHeader(authorizationHeader);

            // Invalidate the token
            jwtService.invalidateToken(jwt);

            return new ResponseEntity<>("Logged out successfully.", HttpStatus.OK);
        } catch (Exception e) {
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
