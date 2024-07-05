package Tasca.S5.__Dice_Game.DB.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/protected")
@RequiredArgsConstructor

public class DemoController {
    @PostMapping(value ="demo")
        public String welcome(){
        return "welcome in restricted area";
    }
}
