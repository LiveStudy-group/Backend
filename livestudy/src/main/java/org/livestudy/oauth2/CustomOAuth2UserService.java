package org.livestudy.oauth2;

import lombok.RequiredArgsConstructor;
import org.livestudy.domain.user.User;
import org.livestudy.exception.CustomException;
import org.livestudy.exception.ErrorCode;
import org.livestudy.repository.UserRepository;
import org.livestudy.security.SecurityUser;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        try {
            return processOAuth2User(userRequest, oauth2User);
        } catch (Exception ex) {
            throw new OAuth2AuthenticationException(ex.getMessage());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, oauth2User.getAttributes());

        //email이 없을 경우 생성
        String email = oAuth2UserInfo.getEmail();
        if (email == null || email.isBlank()) {
            email = oAuth2UserInfo.getProvider().name().toLowerCase()
                    + "_"
                    + oAuth2UserInfo.getId()
                    + "@livestudy.com";
        }

        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
            // 기존 사용자의 소셜 프로바이더가 다른 경우 처리
            if (!user.getSocialProvider().equals(oAuth2UserInfo.getProvider())) {
                throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
            }
            user.setNewUser(false);
        } else {
            user = User.ofSocial(
                    email,
                    generateUniqueNickname(oAuth2UserInfo.getName()),
                    oAuth2UserInfo.getImageUrl(),
                    oAuth2UserInfo.getProvider(),
                    oAuth2UserInfo.getId()
            );
            user.setNewUser(true);
            user = userRepository.save(user);
        }

        // SecurityUser로 반환 (OAuth2User도 구현하므로 문제없음)
        return new SecurityUser(user, oauth2User.getAttributes());
    }

    private User registerNewUser(OAuth2UserInfo oAuth2UserInfo) {
        User user = User.ofSocial(
                oAuth2UserInfo.getEmail(),
                generateUniqueNickname(oAuth2UserInfo.getName()),
                oAuth2UserInfo.getImageUrl(),
                oAuth2UserInfo.getProvider(),
                oAuth2UserInfo.getId()
        );

        return userRepository.save(user);
    }

    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
        // 필요한 경우 사용자 정보 업데이트
        // 예: 프로필 이미지가 변경된 경우
        return existingUser;
    }

    private String generateUniqueNickname(String baseName) {
        if (!StringUtils.hasText(baseName)) {
            baseName = "사용자";
        }

        String nickname = baseName;
        int counter = 1;

        while (userRepository.findByNickname(nickname).isPresent()) {
            nickname = baseName + counter++;
        }

        return nickname;
    }
}