package org.livestudy.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.livestudy.security.SecurityUser;
import org.livestudy.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        log.debug("OAuth2AuthenticationSuccessHandler.onAuthenticationSuccess called.");

        if (response.isCommitted()) {
            log.warn("Response already committed before targetUrl determination. Returning early.");            return;
        }

        String targetUrl = determineTargetUrl(request, response, authentication);
        log.debug("Determined target URL: " + targetUrl);

        if (response.isCommitted()) {
            log.warn("Response already committed after targetUrl determination. Unable to redirect to " + targetUrl);
            return;
        }

        try {
            getRedirectStrategy().sendRedirect(request, response, targetUrl);
            log.debug("Redirected to: " + targetUrl);
        } catch (IOException e) {
            log.error("Failed to redirect to " + targetUrl, e);
            throw e;
        }
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {

        // SecurityUser로 직접 캐스팅 (이미 OAuth2User도 구현하고 있음)
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();

        // JWT 토큰 생성 (기존 방식 그대로 사용)
        String token = jwtTokenProvider.generateToken(authentication);
        log.debug("Generated JWT token for user: " + securityUser.getUsername());

        return UriComponentsBuilder.fromUriString(frontendUrl + "/auth/success")
                .queryParam("token", token)
                .build().toUriString();
    }
}