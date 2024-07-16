package Tasca.S5.__Dice_Game.DB.model.service;

import Tasca.S5.__Dice_Game.DB.model.domain.Game;
import Tasca.S5.__Dice_Game.DB.model.domain.Player;
import Tasca.S5.__Dice_Game.DB.model.domain.Role;
import Tasca.S5.__Dice_Game.DB.model.dto.CustomPlayerDTO;
import Tasca.S5.__Dice_Game.DB.model.dto.PlayerDTO;
import Tasca.S5.__Dice_Game.DB.model.repository.GameRepository;
import Tasca.S5.__Dice_Game.DB.model.repository.PlayerRepository;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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

    @Autowired
    private PlayerMapper playerMapper;


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

        if (!isAdmin) {
            throw new InsufficientAuthenticationException("You don't have permissions to access this resource");
        }

        if (isAdmin && playerDTO.getRole() != null) {
            playerDTO.setRole(playerDTO.getRole());
        }

   //      Determine the role based on the nickname
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

        // Check if the user is not an admin and trying to modify another user's data
        if (!isAdmin && !id.equals(currentUserId)) {
            throw new InsufficientAuthenticationException("You don't have permissions to modify this player's data");
        }

        // Retrieve the player by ID
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Player not found with ID: " + id));

        // Validate and update name
        String newName = playerDTO.getName();
        if (StringUtils.isEmpty(newName)) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (!newName.equals(player.getName())) {
            validateNameUniqueness(newName, id);
            player.setName(newName);
        }

        // Validate and update email
        String newEmail = playerDTO.getEmail();
        if (StringUtils.isEmpty(newEmail)) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (!newEmail.equals(player.getEmail())) {
            validateEmailUniqueness(newEmail, id);
            validateEmailFormat(newEmail);
            player.setEmail(newEmail);
        }

        // Validate and update password
        String newPassword = playerDTO.getPassword();
        if (StringUtils.isEmpty(newPassword)) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        String encodedPassword = passwordEncoder.encode(newPassword);
        player.setPassword(encodedPassword);

        // Guard clause to handle role update
        if (playerDTO.getRole() != null && !isAdmin) {
            throw new InsufficientAuthenticationException("You don't have permissions to modify the player's role");
        }

        // Update role if the user is an admin
        if (playerDTO.getRole() != null) {
            player.setRole(playerDTO.getRole());
        }

        // Save the updated player
        Player updatedPlayer = playerRepository.save(player);

        // Prepare and return the updated PlayerDTO
        double successRate = calculateSuccessRate(updatedPlayer.getId());
        PlayerDTO updatedPlayerDTO = new PlayerDTO(updatedPlayer);
        updatedPlayerDTO.setSuccessRate(successRate);
        updatedPlayerDTO.setTotalPlayedGames(calculateTotalPlayedGames(updatedPlayer.getId()));

        return updatedPlayerDTO;
    }

    private void validateNameUniqueness(String name, String currentUserId) {
        playerRepository.findByNameIgnoreCaseAndIdNot(name, currentUserId)
                .ifPresent(existingPlayer -> {
                    throw new IllegalArgumentException("Player with name " + name + " already exists");
                });
    }

    private void validateEmailUniqueness(String email, String currentUserId) {
        playerRepository.findByEmailIgnoreCaseAndIdNot(email, currentUserId)
                .ifPresent(existingPlayer -> {
                    throw new IllegalArgumentException("Player with email " + email + " already exists");
                });
    }

    private void validateEmailFormat(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }



    @Override
    public CustomPlayerDTO getPlayerById(String id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equalsIgnoreCase("ROLE_ADMIN"));

        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Get One Player Failed: Invalid player id: " + id + " -> DOESN'T EXIST in DataBase"));

        if (!isAdmin && player.getRole() == Role.ROLE_ADMIN) {
            throw new InsufficientAuthenticationException("You don't have permissions to view this player's data");
        }

        return playerMapper.toCustomPlayerDTO(player);
    }

    @Override
    public List<Object> getAllPlayers() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equalsIgnoreCase("ROLE_ADMIN"));

        return playerRepository.findAll().stream()
                .filter(player -> isAdmin || player.getRole() != Role.ROLE_ADMIN) // Exclude ROLE_ADMIN players if the current user is not an admin
                .map(player -> {
                    if (isAdmin) {
                        PlayerDTO playerDTO = new PlayerDTO(player);
                        double successRate = calculateSuccessRate(player.getId());
                        playerDTO.setSuccessRate(successRate);
                        long totalPlayedGames = calculateTotalPlayedGames(player.getId());
                        playerDTO.setTotalPlayedGames(totalPlayedGames);
                        return playerDTO;
                    } else {
                        return playerMapper.toCustomPlayerDTO(player);
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    public String getAverageSuccessRate() {
        List<Player> players = playerRepository.findAll();
        if (players.isEmpty()) {
            throw new EntityNotFoundException("No players found.");
        }

        // Filter out players with the role ROLE_ADMIN
        List<Player> nonAdminPlayers = players.stream()
                .filter(player -> player.getRole() != Role.ROLE_ADMIN)
                .collect(Collectors.toList());

        if (nonAdminPlayers.isEmpty()) {
            throw new EntityNotFoundException("No non-admin players found.");
        }

        double totalSuccessRate = nonAdminPlayers.stream()
                .mapToDouble(player -> calculateSuccessRate(player.getId()))
                .sum();

        double averageSuccessRate = totalSuccessRate / nonAdminPlayers.size();
        long totalGamesPlayed = nonAdminPlayers.stream()
                .mapToLong(player -> gameRepository.countByPlayerId(player.getId()))
                .sum();

        Integer totalNonAdminPlayers = nonAdminPlayers.size();

        return String.format("The success rate is %.2f%% on an overall of %d games played and %d non-admin players registered", averageSuccessRate, totalGamesPlayed, totalNonAdminPlayers);
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
    public CustomPlayerDTO getPlayerWithLowestSuccessRate() {
        return playerRepository.findAll().stream()
                .filter(player -> player.getRole() != Role.ROLE_ADMIN) // Exclude ROLE_ADMIN players
                .min(Comparator.comparingDouble(player -> calculateSuccessRate(player.getId())))
                .map(playerMapper::toCustomPlayerDTO)
                .orElse(null);
    }

    @Override
    public CustomPlayerDTO getPlayerWithHighestSuccessRate() {
        return playerRepository.findAll().stream()
                .filter(player -> player.getRole() != Role.ROLE_ADMIN)
                .max(Comparator.comparingDouble(player -> calculateSuccessRate(player.getId())))
                .map(playerMapper::toCustomPlayerDTO)
                .orElse(null);
    }



    @PostConstruct
    public void createAdminIfNotExists() {
        boolean adminExists = playerRepository.findByEmail("admin@admin.com").isPresent();

        if (!adminExists) {
            Player admin = Player.builder()
                    .email("admin@admin.com")
                    .name("admin")
                    .password(passwordEncoder.encode("admin"))
                    .role(Role.ROLE_ADMIN)
                    .registrationDate(LocalDate.now())
                    .build();

            playerRepository.save(admin);
        }

    }

}
