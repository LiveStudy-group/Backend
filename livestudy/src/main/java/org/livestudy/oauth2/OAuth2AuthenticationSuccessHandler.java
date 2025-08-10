package org.livestudy.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.livestudy.domain.user.SocialProvider;
import org.livestudy.domain.user.User;
import org.livestudy.exception.CustomException;
import org.livestudy.exception.ErrorCode;
import org.livestudy.security.SecurityUser;
import org.livestudy.security.jwt.JwtTokenProvider;
import org.livestudy.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
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

    @Override
    protected String determineTargetUrl(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) {
        SecurityUser principal = (SecurityUser) authentication.getPrincipal();

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(principal,
                        null,
                        principal.getAuthorities());

        // JWT 발급
        String token = jwtTokenProvider.generateToken(authToken);

        boolean isNew = principal.getUser().isNewUser();

        // redirect
        return UriComponentsBuilder.fromUriString(frontendUrl + "/auth/success")
                .queryParam("token", token)
                .queryParam("isNew", isNew)
                .build()
                .toUriString();
    }
}