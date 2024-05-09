package capstone.tunemaker.jwt;

import capstone.tunemaker.dto.CustomMemberDetails;
import capstone.tunemaker.entity.Member;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // request에서 Authorization 헤더를 찾음
        String authorization = request.getHeader("Authorization");

        // 1. Authorization 헤더 검증
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            log.warn("Token is NULL");
            filterChain.doFilter(request, response);

            // 조건이 해당되면 메소드 종료(필수)
            return;
        }

        log.info("authorization now");

        // 2. Bearer 부분 제거 후, 순수 토큰만 흭득(파싱)
        String token = authorization.split(" ")[1];

        // 3. 토큰 소멸 시간 검증
        if (jwtUtil.isExpired(token)) {
            log.warn("Token is Expired");
            filterChain.doFilter(request, response);

            // 조건이 해당되면 메소드 종료(필수)
            return ;
        }

        // 토큰에서 username과 role 흭득
        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        // userEntity를 생성해서 값을 Set
        Member member = new Member();
        member.setUsername(username);
        member.setPassword("temppassword");
        member.setRole(role);

        // userDetails에 회원 정보 객체 담기
        CustomMemberDetails customMemberDetails = new CustomMemberDetails(member);

        // Spring Security 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customMemberDetails, null, customMemberDetails.getAuthorities());

        // 세션에 사용자 등록(임시 저장소)
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }

}