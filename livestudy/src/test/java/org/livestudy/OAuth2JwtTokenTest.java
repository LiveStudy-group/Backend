package org.livestudy;

import org.junit.jupiter.api.Test;
import org.livestudy.domain.user.SocialProvider;
import org.livestudy.domain.user.User;
import org.livestudy.domain.user.UserStatus;
import org.livestudy.security.SecurityUser;
import org.livestudy.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class OAuth2JwtTokenTest {

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Test
    void test_jwt_generation_for_regular_login() {
        // 일반 로그인 사용자
        User user = User.builder()
                .id(1L)
                .email("user@example.com")
                .password("hashedPassword")
                .nickname("일반사용자")
                .socialProvider(SocialProvider.LOCAL)
                .userStatus(UserStatus.NORMAL)
                .build();

        SecurityUser securityUser = new SecurityUser(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                securityUser, null, securityUser.getAuthorities()
        );

        String token = jwtTokenProvider.generateToken(auth);

        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals("user@example.com", jwtTokenProvider.getEmailFromToken(token));

        Authentication parsedAuth = jwtTokenProvider.getAuthentication(token);
        SecurityUser parsedUser = (SecurityUser) parsedAuth.getPrincipal();
        assertEquals(1L, parsedUser.getUser().getId());
        assertEquals("user@example.com", parsedUser.getUser().getEmail());
    }

    @Test
    void test_jwt_generation_for_oauth2_login() {
        // OAuth2 로그인 사용자 (소셜 로그인)
        User socialUser = User.builder()
                .id(2L)
                .email("social@example.com")
                .password(null) // 소셜 로그인은 비밀번호 없음
                .nickname("소셜사용자")
                .profileImage("https://example.com/avatar.jpg")
                .socialProvider(SocialProvider.GOOGLE)
                .userStatus(UserStatus.NORMAL)
                .build();

        // OAuth2 attributes 시뮬레이션
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", "google-user-id");
        attributes.put("email", "social@example.com");
        attributes.put("name", "소셜사용자");
        attributes.put("picture", "https://example.com/avatar.jpg");

        // OAuth2 로그인 시의 SecurityUser
        SecurityUser oAuth2SecurityUser = new SecurityUser(socialUser, attributes);
        Authentication oAuth2Auth = new UsernamePasswordAuthenticationToken(
                oAuth2SecurityUser, null, oAuth2SecurityUser.getAuthorities()
        );

        String token = jwtTokenProvider.generateToken(oAuth2Auth);

        // 같은 JWT 토큰 생성 로직이 잘 동작하는지 확인
        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals("social@example.com", jwtTokenProvider.getEmailFromToken(token));

        Authentication parsedAuth = jwtTokenProvider.getAuthentication(token);
        SecurityUser parsedUser = (SecurityUser) parsedAuth.getPrincipal();
        assertEquals(2L, parsedUser.getUser().getId());
        assertEquals("social@example.com", parsedUser.getUser().getEmail());
    }

    @Test
    void test_oauth2_user_interface_methods() {
        User socialUser = User.builder()
                .id(3L)
                .email("kakao@example.com")
                .nickname("카카오사용자")
                .socialProvider(SocialProvider.KAKAO)
                .userStatus(UserStatus.NORMAL)
                .build();

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("id", 123456789);
        attributes.put("kakao_account", Map.of(
                "email", "kakao@example.com",
                "profile", Map.of("nickname", "카카오사용자")
        ));

        SecurityUser securityUser = new SecurityUser(socialUser, attributes);

        // OAuth2User 인터페이스 메서드들이 잘 동작하는지 확인
        assertEquals(attributes, securityUser.getAttributes());
        assertEquals("3", securityUser.getName()); // User ID를 String으로 반환

        // UserDetails 인터페이스 메서드들도 잘 동작하는지 확인
        assertEquals("kakao@example.com", securityUser.getUsername());
        assertNull(securityUser.getPassword()); // 소셜 로그인은 비밀번호 없음
        assertTrue(securityUser.isEnabled());
        assertTrue(securityUser.isAccountNonLocked());
    }
}