package capstone.tunemaker.jwt;

import capstone.tunemaker.dto.create.CustomMemberDetails;
import capstone.tunemaker.entity.Member;
import capstone.tunemaker.service.TokenBlacklistService;
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

/**
 * JWTFilter는 Login을 했을 때 서버가 준 JWT에 대해, 해당 JWT를 Header에 포함해서 서비스를 요청할 경우, 그 JWT를 검증하는 부분임
 */
@RequiredArgsConstructor
@Slf4j
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // request에서 Authorization 헤더를 찾음
        String authorization = request.getHeader("Authorization");

        // 1. Authorization 헤더 검증
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            log.error("Token is NULL");
            filterChain.doFilter(request, response);

            // 조건이 해당되면 메소드 종료(필수)
            return;
        }

        log.info("Authorization in Header is exist");

        // 2. Bearer 부분 제거 후, 순수 토큰만 흭득(파싱)
        String token = authorization.split(" ")[1];
        log.info("token = {}", token);

        // 3. 토큰 소멸 시간 검증
        if (jwtUtil.isExpired(token) || tokenBlacklistService.isBlacklisted(token)) {

            log.error("Token is Expired");
            // 토큰이 만료되었으므로 401 Unauthorized 상태 코드와 함께 메시지 반환
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(
                    "{\"message\": \"Token is Unavailable. Please log in again.\"}"
            );

            // 조건이 해당되면 메소드 종료(필수)
            return ;
        }

        // 토큰에서 username과 role 흭득
        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);
        Long userId = jwtUtil.getMemberId(token);


        log.info("username={}",username);
        log.info("role={}", role);
        log.info("userId={}", userId);

        // member를 생성해서 값을 Set
        Member member = new Member();
        member.setId(userId);
        member.setUsername(username);
        member.setPassword("temppassword");
        member.setRole(role);

        // userDetails에 회원 정보 객체 담기
        CustomMemberDetails customMemberDetails = new CustomMemberDetails(member);

        // Spring Security 인증 토큰
        // UsernamePasswordAuthenticationToken은 토큰을 만드는 것이 아니라, 사용자의 인증 정보(username, password, role)를 담는 역할
        Authentication authToken = new UsernamePasswordAuthenticationToken(customMemberDetails, null, customMemberDetails.getAuthorities());

        // 세션에 사용자 등록(임시 저장소)
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // 현재 보안 컨텍스트의 Authentication 객체를 가져옴
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Authentication 객체의 상태를 로그로 출력
        log.info("Authentication: " + auth);

        // ########################################## //

        filterChain.doFilter(request, response);
    }

}
