package org.livestudy.websocket.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.livestudy.component.LiveKitTokenVerifier;
import org.livestudy.security.jwt.JwtTokenProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class LiveKitTokenAuthenticationFilter extends OncePerRequestFilter {

    private final LiveKitTokenVerifier liveKitTokenVerifier;
    private final JwtTokenProvider jwtTokenProvider;

    public LiveKitTokenAuthenticationFilter(LiveKitTokenVerifier liveKitTokenVerifier, JwtTokenProvider jwtTokenProvider) {
        this.liveKitTokenVerifier = liveKitTokenVerifier;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                 FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        log.debug("LiveKitTokenAuthenticationFilter - 요청 경로: {}", path);

        if (!path.startsWith("/rtc")) {
            log.debug("LiveKitTokenAuthenticationFilter - /rtc 경로가 아니므로 필터 패스");
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = request.getParameter("access_token");
        log.debug("LiveKitTokenAuthenticationFilter - access_token: {}", accessToken != null ? "[존재]" : "[없음]");

        if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
            Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("LiveKitTokenAuthenticationFilter - 인증 성공, SecurityContext에 저장");
        } else {
            log.warn("LiveKitTokenAuthenticationFilter - 인증 실패, 401 Unauthorized 응답");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
