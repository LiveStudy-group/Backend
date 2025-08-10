package org.livestudy.oauth2;

import lombok.RequiredArgsConstructor;
import org.livestudy.domain.user.SocialProvider;
import org.livestudy.domain.user.User;
import org.livestudy.exception.CustomException;
import org.livestudy.exception.ErrorCode;
import org.livestudy.repository.UserRepository;
import org.livestudy.security.SecurityUser;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {

        OidcUser oidcUser = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // "google"
        OAuth2UserInfo info = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, oidcUser.getAttributes());

        // email이 없을 경우 생성
        String email = info.getEmail();
        if (!StringUtils.hasText(email)) {
            email = info.getProvider().name().toLowerCase()
                    + "_" + info.getId()
                    + "@livestudy.com";
        }

        User user = upsertUser(email, info);

        return new SecurityUser(
                user,
                oidcUser.getAttributes(),
                oidcUser.getIdToken(),
                oidcUser.getUserInfo()
        );
    }

    private User upsertUser(String email, OAuth2UserInfo info) {
        Optional<User> found = userRepository.findByEmail(email);

        if (found.isPresent()) {
            User u = found.get();
            if (!u.getSocialProvider().equals(info.getProvider())) {
                throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
            }
            u.setNewUser(false);
            return u;
        } else {
            User nu = User.ofSocial(
                    email,
                    generateUniqueNickname(info.getName()),
                    info.getImageUrl(),
                    info.getProvider(),
                    info.getId()
            );
            nu.setNewUser(true);
            return userRepository.save(nu);
        }
    }

    private String generateUniqueNickname(String base) {
        if (!StringUtils.hasText(base)) base = "사용자";
        String nick = base;
        int i = 1;
        while (userRepository.findByNickname(nick).isPresent()) {
            nick = base + i++;
        }
        return nick;
    }
}
