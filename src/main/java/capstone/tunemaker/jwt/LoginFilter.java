package capstone.tunemaker.jwt;

import capstone.tunemaker.dto.CustomMemberDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * Spring Security는 내부적으로 필터체인을 사용하여 HTTP 요청을 처리한다.
 *  - 이때 UsernamePasswordAuthenticationFilter를 상속받은 LoginFilter는 “/login” 경로로 들어오는 요청을 자동으로 처리한다.
 *    (기본적으로 UsernamePasswordAuthenticationFilter는 “/login” 경로에 대한 요청을 처리하도록 설정되어있음)
 */
@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    /**
     * 요청을 가로채서 데이터를 추출하는 과정
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try{
            // 아래 Map<String, String>은 LoginDTO로 변환해도 된다.
            Map<String, String> requestBody = new ObjectMapper().readValue(request.getInputStream(), Map.class);

            // 로그인 검증을 email로 한다면, String email = requestBody.get("email"); 로 변경해야 함.
            String username = requestBody.get("username");
            String password = requestBody.get("password");

            // UsernamePasswordAuthenticationToken은 토큰을 만드는 것이 아니라, 사용자의 인증 정보(username, password)를 담는 역할
            // 아래에 있는 null은 ROLE인데, 잠시 null로 지정
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);

            // authenticationManager에게 전달 (authenticationManager는 이후에 DB로부터 User 데이터를 가져와서 authToken에 있는 데이터와 비교해가면서 검증 진행)
            return authenticationManager.authenticate(authToken);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {

        // getPrincipal을 통해 CustomUserDetails 객체를 반환
        CustomMemberDetails customMemberDetails = (CustomMemberDetails) authentication.getPrincipal();

        // authentication으로 부터 username 추출
        String username = customMemberDetails.getUsername();

        // authentication으로 부터 role 추출
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String role = auth.getAuthority();

        // JWT 생성 부분
        String token = jwtUtil.createJwt(username, role, 60*60*10L);

        // HTTP RFC 7235 인증 방식에 맞춰 포맷 설정
        // Authorization : 타입 인증 토근
        //    Ex) Authorization : Bearer 인증토큰String
        response.addHeader("Authorization", "Bearer " + token);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setStatus(401);
    }

}
