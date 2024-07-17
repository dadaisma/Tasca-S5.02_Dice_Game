package Tasca.S5.__Dice_Game.DB.controllers;


import Tasca.S5.__Dice_Game.DB.model.dto.CustomPlayerDTO;
import Tasca.S5.__Dice_Game.DB.model.dto.PlayerDTO;
import Tasca.S5.__Dice_Game.DB.model.service.PlayerService;
import Tasca.S5.__Dice_Game.DB.utils.HeaderUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @Operation(summary = "ADMIN can register a new player")
    @PostMapping
    public ResponseEntity<PlayerDTO>createPlayer(@RequestBody PlayerDTO playerDTO) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
        throw new InsufficientAuthenticationException("You don't have permissions to access this resource");
        }

       PlayerDTO userCreated = playerService.createPlayer(playerDTO);
        return new ResponseEntity<>(userCreated, createHeaders(token), HttpStatus.CREATED) ;
    }

    @Operation(summary = "edit a player by ID, user can edit name/email/pwd of himself while ADMIN can edit anyone and change roles also")
    @PutMapping("/{id}")
    public ResponseEntity<PlayerDTO> updatePlayerName(@PathVariable String id, @RequestBody PlayerDTO playerDTO) {
        PlayerDTO editUser = playerService.updatePlayerName(id, playerDTO);

        return new ResponseEntity<>(editUser, createHeaders(HeaderUtil.getToken()), HttpStatus.OK);
    }

    //bearer back
    @Operation(summary = "GET all players")
    @GetMapping
   // @PreAuthorize("hasRole('ROLE_ROLE_ADMIN')")
    public ResponseEntity<List<Object>> getAllPlayers(HttpServletRequest request) {

        String token = request.getHeader("Authorization");

        List<Object> players = playerService.getAllPlayers();
        return new ResponseEntity<>(players, createHeaders(token), HttpStatus.OK);
    }

    @Operation(summary = "find a player by ID")
    @GetMapping("/{id}")
    public ResponseEntity<CustomPlayerDTO> getPlayerById(@PathVariable String id) {
        CustomPlayerDTO userById = playerService.getPlayerById(id);
        return new ResponseEntity<>(userById, HttpStatus.OK);
    }

    @Operation(summary = "check total games played and % of success overall")
    @GetMapping("/ranking")
    public ResponseEntity<String> getAverageSuccessRate() {
        String averageSuccessRate = playerService.getAverageSuccessRate();
        return new ResponseEntity<>(averageSuccessRate,  HttpStatus.OK);
    }

    @Operation(summary = "list worst player")
    @GetMapping("/ranking/loser")
    public ResponseEntity<CustomPlayerDTO> getPlayerWithLowestSuccessRate() {
        CustomPlayerDTO loser = playerService.getPlayerWithLowestSuccessRate();
        return new ResponseEntity<>(loser,  HttpStatus.OK);
    }

    @Operation(summary = "list better player")
    @GetMapping("/ranking/winner")
    public ResponseEntity<CustomPlayerDTO> getPlayerWithHighestSuccessRate() {
        CustomPlayerDTO winner = playerService.getPlayerWithHighestSuccessRate();

        return new ResponseEntity<>(winner, HttpStatus.OK);
    }


}
