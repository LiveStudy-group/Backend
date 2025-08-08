package org.livestudy.service;

import org.livestudy.dto.UserProfile.*;

public interface ProfileService {

    UserProfileResponse getUserProfile(Long userId);

    void updateNickname(Long userId, UpdateNicknameRequest request);

    void updateProfileImage(Long userId, String newProfileImage);

    void updateEmail(Long userId, UpdateEmailRequest request);

    void updatePassword(Long userId, UpdatePasswordRequest request);
}
