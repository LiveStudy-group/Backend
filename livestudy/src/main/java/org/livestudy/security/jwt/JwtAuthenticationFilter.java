package org.livestudy.security.jwt;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
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

        String token = resolveToken(request);

//        String path = request.getRequestURI();
//        if(path.startsWith("/ws")){
//                 filterChain.doFilter(request, response);
//                 return;
//             }

        if(token != null && jwtTokenProvider.validateToken(token)) {
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    // Authorization에서 JWT 값을 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if(bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        String accessToken = request.getParameter("access_token");
        if(accessToken != null && !accessToken.isEmpty()) {
            return accessToken;
        }

        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        // WebSocket 연결할 때 필터를 실행하지 않음
        if(path.startsWith("/ws") || path.startsWith("/rtc"))
            return true;
        // OAuth2 인증 관련 경로와 로그인/회원가입 API 경로에서는 필터를 실행하지 않음
        return path.startsWith("/oauth2/") || path.startsWith("/api/auth/");
    }

    
}
