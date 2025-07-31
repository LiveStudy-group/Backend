package org.livestudy.dto.UserProfile;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserProfileResponse {
    private String profileImage;
    private String nickname;
    private String email;
    private String selectedTitle;
    private Integer totalStudyTime;
}
