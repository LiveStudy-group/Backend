package org.livestudy.security.jwt;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;


    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, java.io.IOException {

        log.debug("[JwtAuthenticationFilter] 요청 URI: {}", request.getRequestURI());

        String token = resolveToken(request);


        log.debug("[JwtAuthenticationFilter] 추출된 토큰: {}", token != null ? token : "없음");


        if(token != null && jwtTokenProvider.validateToken(token)) {
            if (jwtTokenProvider.validateToken(token)) {
                log.debug("[JwtAuthenticationFilter] 토큰 유효성 검증 성공");
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                log.debug("[JwtAuthenticationFilter] Authentication 객체 생성: {}", authentication);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("[JwtAuthenticationFilter] SecurityContext에 Authentication 저장 완료");
            } else {
                log.warn("[JwtAuthenticationFilter] 토큰 유효성 검증 실패");
            }
        } else {
            log.debug("[JwtAuthenticationFilter] 토큰이 없으므로 인증 처리 건너뜀");
        }

        filterChain.doFilter(request, response);
    }

    // Authorization에서 JWT 값을 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        log.debug("[JwtAuthenticationFilter] Authorization 헤더: {}", bearerToken);

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String extractedToken = bearerToken.substring(7);
            log.debug("[JwtAuthenticationFilter] Bearer 토큰 추출 완료");
            return extractedToken;
        }

        String paramToken = request.getParameter("access_token");
        log.debug("[JwtAuthenticationFilter] access_token 헤더: {}", paramToken);
        if (paramToken != null) {
            log.debug("[JwtAuthenticationFilter] access_token 토큰 추출 완료");
            return paramToken;
        }

        log.debug("[JwtAuthenticationFilter] 적용 실패");
        return null;

    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        boolean skip = path.startsWith("/oauth2/") || path.startsWith("/api/auth/");
        if (skip) {
            log.debug("[JwtAuthenticationFilter] shouldNotFilter 적용: {} → 필터 스킵", path);
        }

        // 스킵할 경로 명확히 지정
        if (path.startsWith("/oauth2/") || path.startsWith("/api/auth/") || path.contains("/api/study-rooms/rtc")) {
            log.debug("[JwtAuthenticationFilter] shouldNotFilter 적용: {} → 필터 스킵", path);
            return true;
        }

        return false; // 그 외 요청은 필터 실행
    }

    
}
