package org.livestudy.oauth2;

import org.livestudy.domain.user.SocialProvider;
import org.livestudy.exception.CustomException;
import org.livestudy.exception.ErrorCode;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if (registrationId.equalsIgnoreCase(SocialProvider.GOOGLE.toString())) {
            return new GoogleOAuth2UserInfo(attributes);
        } else if (registrationId.equalsIgnoreCase(SocialProvider.KAKAO.toString())) {
            return new KakaoOAuth2UserInfo(attributes);
        } else if (registrationId.equalsIgnoreCase(SocialProvider.NAVER.toString())) {
            return new NaverOAuth2UserInfo(attributes);
        } else {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }
    }
}