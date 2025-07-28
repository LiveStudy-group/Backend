package org.livestudy.service;

import lombok.RequiredArgsConstructor;
import org.livestudy.domain.user.User;
import org.livestudy.dto.SocialLoginResponse;
import org.livestudy.security.SecurityUser;
import org.livestudy.security.jwt.JwtTokenProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SocialLoginService {

    private final JwtTokenProvider jwtTokenProvider;

    public SocialLoginResponse createSocialLoginResponse(SecurityUser userPrincipal, boolean isNewUser) {
        User user = userPrincipal.getUser();

        // SecurityUser로 JWT 토큰 생성
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());

        String token = jwtTokenProvider.generateToken(authToken);

        return SocialLoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .socialProvider(user.getSocialProvider())
                .isNewUser(isNewUser)
                .build();
    }
}