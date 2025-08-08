package org.livestudy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.livestudy.domain.user.SocialProvider;
import org.livestudy.domain.user.User;
import org.livestudy.dto.UserLoginRequest;
import org.livestudy.dto.UserLoginResponse;
import org.livestudy.dto.UserSignupRequest;
import org.livestudy.exception.CustomException;
import org.livestudy.exception.ErrorCode;
import org.livestudy.repository.UserRepository;
import org.livestudy.security.jwt.JwtTokenProvider;
import org.livestudy.service.UserServiceImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtProvider;

    @Test
    void signup_success(){
        // given
        UserSignupRequest request = UserSignupRequest.builder()
                .email("test@example.com")
                .password("123456")
                .nickname("test")
                .introduction("testing")
                .profileImage("https://example.com/image.jpg")
                .socialProvider(SocialProvider.LOCAL)
                .build();

        String encodedPassword = "encodedPassword";

        User savedUser = User.builder()
                .id(1L)  // id 꼭 넣기
                .email(request.getEmail())
                .password(encodedPassword)
                .nickname(request.getNickname())
                .introduction(request.getIntroduction())
                .profileImage(request.getProfileImage())
                .socialProvider(SocialProvider.LOCAL)
                .build();

        // mocking behavior
        when(passwordEncoder.encode(request.getPassword())).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // when
        Long result = userService.signup(request);

        // then
        assertThat(result).isEqualTo(1L);
        verify(passwordEncoder).encode(request.getPassword());  // 원본 비밀번호로 검증
        verify(userRepository).save(any(User.class));
    }

    @Test
    void signup_fail_duplicate_email_throws_exception(){
        // given

        UserSignupRequest request = UserSignupRequest.builder()
                .email("test@example.com")
                .password("123456")
                .nickname("test")
                .introduction("testing")
                .profileImage("https://example.com/image.jpg")
                .socialProvider(SocialProvider.LOCAL)
                .build();

        User existingUser = User.builder()
                .email(request.getEmail())
                .password("334456")
                .nickname(request.getNickname())
                .build();




        // 이미 해당 사용자의 이메일이 겹치는 경우
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(existingUser));

        CustomException exception = assertThrows(CustomException.class,
                () -> userService.signup(request));

        assertThat(exception.getMessage()).contains("이미 존재하는 이메일입니다.");
    }

    @Test
    void login_success() {
        // given
        String rawPassword = "123456";
        String encodedPassword = "encodedPassword";
        String expectedToken = "jwt-token";
        String email = "test@example.com";


        UserLoginRequest request = new UserLoginRequest(email, rawPassword);

        User user = User.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .build();

        when(userRepository.findByEmail(request.getEmail())).
                thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).
                thenReturn(true);
        when(jwtProvider.generateToken(any(Authentication.class))).thenReturn(expectedToken);

        // when
        UserLoginResponse result = userService.login(request);

        // then
        assertThat(result).isEqualTo(new UserLoginResponse(expectedToken));

    }

    // 없는 사용자가 로그인을 했을 경우
    @Test
    void login_fail_user_not_found() {
        // given
        UserLoginRequest request = new UserLoginRequest("nonexistent@example.com", "password");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());
    }

    // 비밀번호가 일치하지 않을 경우
    @Test
    void login_fail_invalid_password() {
        // given
        String email = "test@example.com";
        String rawPassword = "wrongPassword";
        String encodedPassword = "encodedCorrectPassword";

        UserLoginRequest request = new UserLoginRequest(email, rawPassword);
        User user = User.builder()
                .email(email)
                .password(encodedPassword)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.INVALID_PASSWORD.getMessage());
    }



}
