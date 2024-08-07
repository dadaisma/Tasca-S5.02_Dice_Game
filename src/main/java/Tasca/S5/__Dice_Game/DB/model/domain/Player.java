package Tasca.S5.__Dice_Game.DB.model.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder

@Document(collection = "player")
public class Player implements UserDetails {

    @Id
    private String id;
    private String name;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate registrationDate;
    private Role role;
    @NonNull
    private String email;
    @NonNull
    private String password;


    public Player(String name, @NonNull String email, @NonNull String password, Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.registrationDate = LocalDate.now();
        this.role = role;
    }



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
