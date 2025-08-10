package org.livestudy.oauth2;

import org.livestudy.domain.user.SocialProvider;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Map;

public class NaverOAuth2UserInfo extends OAuth2UserInfo {

    private final Map<String, Object> response;

    public NaverOAuth2UserInfo(Map<String, Object> attributes) {

        super(attributes);
        Object resp = attributes.get("response");
        this.response = (resp instanceof Map)
                ? (Map<String, Object>) resp
                : Collections.emptyMap();
    }

    @Override
    public String getId() {
        if (response == null) {
            return null;
        }
        return (String) response.get("id");
    }

    @Override
    public String getName() {
        if (response == null) {
            return null;
        }
        String nickname = (String) response.get("nickname");
        String name = (String) response.get("name");

        return StringUtils.hasText(nickname) ? nickname : name;
    }

    @Override
    public String getEmail() {
        if (response == null) {
            return null;
        }
        return (String) response.get("email");
    }

    @Override
    public String getImageUrl() {
        if (response == null) {
            return null;
        }
        return (String) response.get("profile_image");
    }

    @Override
    public SocialProvider getProvider() {
        return SocialProvider.NAVER;
    }
}