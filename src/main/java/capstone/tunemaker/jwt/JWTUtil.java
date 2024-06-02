package capstone.tunemaker.jwt;

import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Slf4j
public class JWTUtil {

    private SecretKey secretKey; // Token 생성 시에 전자 서명 목적, 클라이언트로부터 Token 받았을 때 검증하기 위한 목적

    public JWTUtil(@Value("${spring.jwt.secret}") String secret) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public Long getMemberId(String token){
        return Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token)
                .getPayload().get("userId", Long.class);
    }

    public String getUsername(String token){
        return Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token)
                .getPayload().get("username", String.class);
    }

    public String getRole(String token){
        return Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token)
                .getPayload().get("role", String.class);
    }

    public Boolean isExpired(String token){
        return Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token)
                .getPayload().getExpiration()
                .before(new Date());
    }

    public String createJwt(String username, String role,Long userId, Long expireMs){
        log.info("expire time = {}", new Date(System.currentTimeMillis() + expireMs));
        return Jwts.builder()
                .claim("username", username)
                .claim("role", role)
                .claim("userId", userId)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expireMs))
                .signWith(secretKey)
                .compact();
    }
}
