package capstone.tunemaker.jwt;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtil {

    private SecretKey secretKey; // Token 생성 시에 전자 서명 목적 / 클라이언트로부터 Token 받았을 때 검증하기 위한 목적

    /**
     * secretKey 생성자(생성 부분)
     */
    public JWTUtil(@Value("${spring.jwt.secret}") String secret) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    /**
     * 클라이언트로부터 받은 토큰을 검증하는 부분
     *  1. getXXX
     *      Jwts.parser().verifyWith(secretKey).build() : JWT 토큰을 파싱하고 secretKey를 사용해서 토큰의 서명을 검증
     *      .parseSignedClaims(token) : JWT 파싱하여, JWT 토큰의 페이로드에 접근할 수 있는 메서드를 제공하는 Claims 객체로 반환
     *      .getPayload().get("XXX", String.class) : 만들어진 Claims 객체에서 XXX를 추출하고, 문자열로 변환하여 반환
     *  2. isExpired
     *       현재 시간값과 비교하여 만료되었는지 판단
     */
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

    /**
     * 토큰 생성 부분
     *  - claim      :  Claim 객체안에 이름에 맞는 값 설정
     *  - issueAt    :  Token을 언제 발행했는지 설정
     *  - expiration :  Token의 유효기간이 얼마인지 설정
     *  
     *  - signWith   :  서버가 발행한 Token이라는 것을 기록하기 위해 디지털 서명 수행
     *       ㄴ signWith는 Payload에 저장되는 값이 아니라, 단순 전자서명하는 기능임
     */
    public String createJwt(String username, String role, Long expireMs){
        return Jwts.builder()
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() * expireMs))
                .signWith(secretKey)
                .compact();
    }
}
