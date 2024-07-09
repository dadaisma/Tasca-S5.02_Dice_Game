package Tasca.S5.__Dice_Game.DB.security;


import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.security.core.GrantedAuthority;
import java.security.Key;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
public class JwtService {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private Long jwtExpiration;

    private Set<String> invalidatedTokens = new HashSet<>();

    public String getToken(UserDetails user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getAuthorities().stream()
                .map(authority ->
                      //  "ROLE_" +
                                authority.getAuthority())
                .collect(Collectors.toList()));
        return getToken(claims, user);
    }

    private String getToken(Map<String, Object> extraClaims, UserDetails user) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + getExpirationTime()))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    public String getUsernameFromToken(String token) {
        return getClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String email = getUsernameFromToken(token);
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private Claims getAllClaims(String token){
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public <T> T getClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims =  getAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Date getExpiration(String token){
        return getClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token){
        return getExpiration(token).before(new Date());
    }
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    public long getExpirationTime() {
        return jwtExpiration;
    }

    public boolean validateToken(String token) {
        if (invalidatedTokens.contains(token)) {
            return false;
        }

        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public String invalidateToken(String token) {
        // Decode the token to get its claims
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        // Set the expiration date to one day in the past
        claims.setExpiration(new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000));

        // Rebuild the token with the updated claims
        String invalidatedToken = Jwts.builder()
                .setClaims(claims)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();

        // Add the invalidated token to the blacklist
        invalidatedTokens.add(invalidatedToken);


        return invalidatedToken;
    }

}

