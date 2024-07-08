package Tasca.S5.__Dice_Game.DB.controllers;

import Tasca.S5.__Dice_Game.DB.model.dto.GameDTO;
import Tasca.S5.__Dice_Game.DB.model.dto.PlayerDTO;
import Tasca.S5.__Dice_Game.DB.model.service.PlayerService;
import Tasca.S5.__Dice_Game.DB.security.JwtService;
import Tasca.S5.__Dice_Game.DB.utils.HeaderUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static Tasca.S5.__Dice_Game.DB.utils.HeaderUtil.createHeaders;


@RestController
@RequestMapping("/players")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    String token = HeaderUtil.getToken();

    @PostMapping
    public ResponseEntity<PlayerDTO>createPlayer(@RequestBody PlayerDTO playerDTO) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
        throw new InsufficientAuthenticationException("You don't have permissions to access this resource");
        }

       PlayerDTO userCreated = playerService.createPlayer(playerDTO);
        return new ResponseEntity<>(userCreated, createHeaders(token), HttpStatus.CREATED) ;
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlayerDTO> updatePlayerName(@PathVariable String id, @RequestBody PlayerDTO playerDTO) {
        PlayerDTO editUser = playerService.updatePlayerName(id, playerDTO);

        return new ResponseEntity<>(editUser, createHeaders(HeaderUtil.getToken()), HttpStatus.OK);
    }

    //bearer back
    @GetMapping
   // @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<PlayerDTO>> getAllPlayers(HttpServletRequest request) {

        String token = request.getHeader("Authorization");

        List<PlayerDTO> players = playerService.getAllPlayers();
        return new ResponseEntity<>(players, createHeaders(token), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerDTO> getPlayerById(@PathVariable String id) {
        PlayerDTO userById = playerService.getPlayerById(id);
        return new ResponseEntity<>(userById, createHeaders(token), HttpStatus.OK);
    }

    @GetMapping("/ranking")
    public ResponseEntity<String> getAverageSuccessRate() {
        String averageSuccessRate = playerService.getAverageSuccessRate();
        return new ResponseEntity<>(averageSuccessRate, createHeaders(token), HttpStatus.OK);
    }

    @GetMapping("/ranking/loser")
    public ResponseEntity<PlayerDTO> getPlayerWithLowestSuccessRate() {
        PlayerDTO loser = playerService.getPlayerWithLowestSuccessRate();
        return new ResponseEntity<>(loser, createHeaders(token), HttpStatus.OK);
    }

    @GetMapping("/ranking/winner")
    public ResponseEntity<PlayerDTO> getPlayerWithHighestSuccessRate() {
        PlayerDTO winner = playerService.getPlayerWithHighestSuccessRate();
        return new ResponseEntity<>(winner, createHeaders(token),HttpStatus.OK);
    }


}
