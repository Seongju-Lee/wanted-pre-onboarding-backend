package preonboarding.board.configuration.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtProvider {

    private final Long tokenExpirationTime;
    private final String key;
    private final String issuer;

    public JwtProvider(
            @Value("${jwt.expiration-key}") Long tokenExpirationTime,
            @Value("${jwt.issuer}") String issuer,
            @Value("${jwt.secret-key}") String key
    ) {
        this.tokenExpirationTime = tokenExpirationTime;
        this.issuer = issuer;
        this.key = key;
    }

    public String createToken(String email, String role) {
        Claims claims = Jwts.claims();
        claims.put("email", email);
        claims.put("role", role);

        long currentTimeMillis = System.currentTimeMillis();
        return Jwts.builder()
                .setIssuer(issuer)
                .setClaims(claims)
                .setIssuedAt(new Date(currentTimeMillis))
                .setExpiration(new Date(currentTimeMillis + tokenExpirationTime))
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

    public void validationToken(String token) {
        Jwts.parser().setSigningKey(key).parseClaimsJws(token);
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();
    }
}