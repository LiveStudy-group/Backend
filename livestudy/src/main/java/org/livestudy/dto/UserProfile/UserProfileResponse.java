package org.livestudy.dto.UserProfile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "유저 프로필 응답 DTO")
public class UserProfileResponse {

    @Schema(description = "프로필 이미지 주소", example = "https://newImage.com/profile.png")
    private String profileImage;

    @Schema(description = "유저 닉네임", example = "열공이")
    private String nickname;

    @Schema(description = "유저 이메일", example = "new@example.com")
    private String email;

    @Schema(description = "유저 장착 칭호", example = "NIGHT_OWL")
    private String selectedTitle;

    @Schema(description = "누적 공부 시간", example = "23566")
    private Integer totalStudyTime;

    @Schema(description = "누적 출석일", example = "121")
    private Integer totalAttendanceDays;

    @Schema(description = "연속 출석일", example = "23")
    private Integer continueAttendanceDays;
}
