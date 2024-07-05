package Tasca.S5.__Dice_Game.DB.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;

public class HeaderUtil {


    @Getter
    private static String token;

    public static HttpHeaders createHeaders(String token) {
        System.out.println("Token received: " + token);
        HttpHeaders headers = new HttpHeaders();
        if (token != null) {
            headers.add("Authorization", "Bearer " + token);
        }
        return headers;
    }

    public static void setToken(String token) {
        HeaderUtil.token = token;
    }
}