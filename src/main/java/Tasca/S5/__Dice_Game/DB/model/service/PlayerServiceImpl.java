package Tasca.S5.__Dice_Game.DB.model.service;

import Tasca.S5.__Dice_Game.DB.model.domain.Game;
import Tasca.S5.__Dice_Game.DB.model.domain.Player;
import Tasca.S5.__Dice_Game.DB.model.domain.Role;
import Tasca.S5.__Dice_Game.DB.model.dto.PlayerDTO;
import Tasca.S5.__Dice_Game.DB.model.repository.GameRepository;
import Tasca.S5.__Dice_Game.DB.model.repository.PlayerRepository;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Transactional
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public PlayerServiceImpl(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Autowired
    private GameRepository gameRepository;


    @Override
    public PlayerDTO createPlayer(PlayerDTO playerDTO) {
        if (StringUtils.isEmpty(playerDTO.getEmail())) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(playerDTO.getEmail());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }

        if (StringUtils.isEmpty(playerDTO.getPassword())) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        String encodedPassword = passwordEncoder.encode(playerDTO.getPassword());


        if (playerDTO.getName() == null || playerDTO.getName().isEmpty()) {
            playerDTO.setName("ANÒNIM");
        }

        // Check if a player with the same name already exists (excluding "ANÒNIM")
        if (!playerDTO.getName().equals("ANÒNIM") && playerRepository.findByName(playerDTO.getName()).isPresent()) {
            throw new EntityExistsException("Create new Player Failed: Invalid Player name: " + playerDTO.getName() + " -> ALREADY EXISTS in DataBase");
        }

        if (playerRepository.findByEmail(playerDTO.getEmail()).isPresent()) {
            throw new EntityExistsException("Create new Player Failed: Invalid email: " + playerDTO.getEmail() + " -> ALREADY EXISTS in DataBase");
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserId = ((Player) authentication.getPrincipal()).getId();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equalsIgnoreCase("ROLE_ADMIN"));


        if (isAdmin && playerDTO.getRole() != null) {
            playerDTO.setRole(playerDTO.getRole());
        }

        // Determine the role based on the nickname
        Role role = Role.ROLE_USER; // Default role
        if (playerDTO.getName() != null && playerDTO.getName().toLowerCase().contains("admin")) {
            role = Role.ROLE_ADMIN;
        }

        // Update the role in the playerDTO
        playerDTO.setRole(role);

        // Create a new Player entity from the provided PlayerDTO
        Player player = new Player(playerDTO.getName(), playerDTO.getEmail(), encodedPassword, playerDTO.getRole());


        try {
            // Save the new player to the repository
            Player savedPlayer = playerRepository.save(player);

            // Count total played games in MySQL for the saved player
            long totalPlayedGames = gameRepository.countByPlayerId(savedPlayer.getId());

            // Create and return a PlayerDTO from the saved Player entity and total played games
            PlayerDTO savedPlayerDTO = new PlayerDTO(savedPlayer);
            savedPlayerDTO.setTotalPlayedGames(totalPlayedGames);
            return savedPlayerDTO;

        } catch (Exception e) {

            throw new RuntimeException("Failed to create player: " + e.getMessage(), e);
        }
    }


    @Override
        public PlayerDTO updatePlayerName(String id, PlayerDTO playerDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserId = ((Player) authentication.getPrincipal()).getId();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equalsIgnoreCase("ROLE_ADMIN"));


        if (!isAdmin && !id.equals(currentUserId)) {
            throw new InsufficientAuthenticationException("You don't have permissions to modify this player's data");
        }


        Player player = playerRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Update Player Failed: Invalid player id: " + id + " -> DOESN'T EXIST in DataBase"));

            if (StringUtils.isEmpty(playerDTO.getName())) {
                throw new IllegalArgumentException("Name cannot be empty");
            }
            if (StringUtils.isEmpty(playerDTO.getEmail())) {
                throw new IllegalArgumentException("Email cannot be empty");
            }

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(playerDTO.getEmail());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }


            if (StringUtils.isEmpty(playerDTO.getPassword())) {
                throw new IllegalArgumentException("Password cannot be empty");
            }
            String newPassword = playerDTO.getPassword();
            String encodedPassword = passwordEncoder.encode(newPassword);

            player.setName(playerDTO.getName());
            player.setEmail(playerDTO.getEmail());
            player.setPassword(encodedPassword);


            if (isAdmin && playerDTO.getRole() != null) {
            player.setRole(playerDTO.getRole());
            }


            Player updatedPlayer = playerRepository.save(player);

            double successRate = calculateSuccessRate(player.getId());
            PlayerDTO updatedPlayerDTO = new PlayerDTO(updatedPlayer);
            updatedPlayerDTO.setSuccessRate(successRate);
            updatedPlayerDTO.setTotalPlayedGames(calculateTotalPlayedGames(updatedPlayer.getId()));

            return updatedPlayerDTO;
        }



    public PlayerDTO getPlayerById(String id) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Get One Player Failed: Invalid player id: " + id + " -> DOESN'T EXIST in DataBase"));

        PlayerDTO playerDTO = new PlayerDTO(player);
        double successRate = calculateSuccessRate(player.getId());
        playerDTO.setSuccessRate(successRate);
        playerDTO.setTotalPlayedGames(calculateTotalPlayedGames(player.getId()));
        return playerDTO;
    }

    @Override
    public List<PlayerDTO> getAllPlayers() {
        return playerRepository.findAll().stream()
                .map(player -> {
                    PlayerDTO playerDTO = new PlayerDTO(player);
                    double successRate = calculateSuccessRate(player.getId());
                    playerDTO.setSuccessRate(successRate);
                    long totalPlayedGames = calculateTotalPlayedGames(player.getId());
                    playerDTO.setTotalPlayedGames(totalPlayedGames);
                    return playerDTO;
                })
                .collect(Collectors.toList());
    }

    private List<Game> getGamesForPlayer(String playerId) {
        Optional<List<Game>> gamesOpt = gameRepository.findByPlayerId(playerId);
        return gamesOpt.orElse(Collections.emptyList());
    }

    @Override
    public String getAverageSuccessRate() {
        List<Player> players = playerRepository.findAll();
        if (players.isEmpty()) {
            throw new EntityNotFoundException( "No players found.");
        }

        double totalSuccessRate = players.stream()
                .mapToDouble(player ->
                    calculateSuccessRate(player.getId())).sum();

        double averageSuccessRate = totalSuccessRate / players.size();
        long totalGamesPlayed = players.stream()
                .mapToLong(player -> gameRepository.countByPlayerId(player.getId()))
                .sum();

        return String.format("The success rate is %.2f %% on an overall of  %d games played", averageSuccessRate, totalGamesPlayed);
    }

    private double calculateSuccessRate(String playerId) {
        long totalGames = gameRepository.countByPlayerId(playerId);
        long wonGames = gameRepository.countByPlayerIdAndWon(playerId, true);
        return totalGames == 0 ? 0 : (double) wonGames / totalGames * 100;
    }

    @Override
    public long calculateTotalPlayedGames(String playerId) {
        return gameRepository.countByPlayerId(playerId);
    }


    @Override
    public PlayerDTO getPlayerWithLowestSuccessRate() {
        return playerRepository.findAll().stream()
                .min(Comparator.comparingDouble(player -> calculateSuccessRate(player.getId())))
                .map(player -> {
                    PlayerDTO playerDTO = new PlayerDTO(player);
                    playerDTO.setSuccessRate(calculateSuccessRate(player.getId()));
                    playerDTO.setTotalPlayedGames(gameRepository.countByPlayerId(player.getId()));
                    return playerDTO;
                })
                .orElse(null);
    }

    @Override
    public PlayerDTO getPlayerWithHighestSuccessRate() {
        return playerRepository.findAll().stream()
                .max(Comparator.comparingDouble(player -> calculateSuccessRate(player.getId())))
                .map(player -> {
                    PlayerDTO playerDTO = new PlayerDTO(player);
                    playerDTO.setSuccessRate(calculateSuccessRate(player.getId()));
                    playerDTO.setTotalPlayedGames(gameRepository.countByPlayerId(player.getId()));
                    return playerDTO;
                })
                .orElse(null);
    }


}
