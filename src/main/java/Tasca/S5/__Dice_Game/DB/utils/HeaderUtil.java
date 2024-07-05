package Tasca.S5.__Dice_Game.DB.utils;

import org.springframework.http.HttpHeaders;

public class HeaderUtil {
    public static HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("token", "token-value");
        return headers;
    }
}
