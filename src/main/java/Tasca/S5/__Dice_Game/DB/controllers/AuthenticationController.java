package Tasca.S5.__Dice_Game.DB.controllers;

import Tasca.S5.__Dice_Game.DB.dao.LoginRequest;
import Tasca.S5.__Dice_Game.DB.dao.RegisterRequest;
import Tasca.S5.__Dice_Game.DB.dao.response.JwtAuthenticationResponse;
import Tasca.S5.__Dice_Game.DB.model.service.AuthService;
import Tasca.S5.__Dice_Game.DB.utils.HeaderUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor


public class AuthenticationController {

    private final AuthService authService;

    @PostMapping(value = "login")
    public ResponseEntity<JwtAuthenticationResponse> login(@RequestBody LoginRequest request) {
        JwtAuthenticationResponse response = authService.login(request);
        HttpHeaders headers = HeaderUtil.createHeaders(response.getToken());
        return ResponseEntity.ok().headers(headers).body(response);
    }

    @PostMapping(value = "register")
    public ResponseEntity<JwtAuthenticationResponse> signUp(@RequestBody RegisterRequest request){
        JwtAuthenticationResponse response = authService.signUp(request);
        HttpHeaders headers = HeaderUtil.createHeaders(response.getToken());
        return ResponseEntity.ok().headers(headers).body(response);
    }

}
