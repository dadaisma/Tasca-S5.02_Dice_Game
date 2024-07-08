package Tasca.S5.__Dice_Game.DB.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/protected")
@RequiredArgsConstructor

public class DemoController {
    @PostMapping(value ="demo")
        public String welcome(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authorities: " + authentication.getAuthorities()); // Print authorities for debugging

        // Check if user has ROLE_ADMIN role
        if (!authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new InsufficientAuthenticationException("You don't have permissions to access this resource");
        }

        return "welcome in restricted area";
    }
}
