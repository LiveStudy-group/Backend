package org.livestudy.service;

import lombok.RequiredArgsConstructor;
import org.livestudy.domain.user.SocialProvider;
import org.livestudy.domain.user.User;
import org.livestudy.domain.user.UserStatus;
import org.livestudy.dto.UserLoginRequest;
import org.livestudy.dto.UserLoginResponse;
import org.livestudy.dto.UserSignupRequest;
import org.livestudy.exception.CustomException;
import org.livestudy.exception.ErrorCode;
import org.livestudy.repository.UserRepository;
import org.livestudy.security.SecurityUser;
import org.livestudy.security.jwt.JwtTokenProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    // 회원가입
    @Override
    public Long signup(UserSignupRequest request) {

        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        });

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .introduction(request.getIntroduction())
                .profileImage(request.getProfileImage())
                .socialProvider(SocialProvider.LOCAL)
                .userStatus(UserStatus.NORMAL)
                .build();

        User savedUser = userRepository.save(user);
        return savedUser.getId();
    }

    // 통상 이메일 로그인
    @Override
    public UserLoginResponse login(UserLoginRequest request) {
        // 1. 이메일로 사용자 조회
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 2. 비밀번호 확인
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        // 3. SecurityUser로 객체 생성
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                new SecurityUser(user), null, new SecurityUser(user).getAuthorities()
        );

        // 4. token 생성
        String token = jwtTokenProvider.generateToken(authentication);

        // 5. 응답 객체 구성
        return new UserLoginResponse(token);
    }
}
