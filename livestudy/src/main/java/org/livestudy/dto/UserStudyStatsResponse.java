package org.livestudy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.livestudy.domain.user.UserStudyStat;

import java.time.LocalDate;

@Getter
@Builder
@Schema(description = "유저 공부 데이터 응답 DTO")
public class UserStudyStatsResponse {

    @Schema(description = "유저 ID", example = "1L")
    private Long userId;

    @Schema(description = "유저 닉네임", example = "열공이")
    private String nickname;

    @Schema(description = "누적 공부 시간", example = "232334")
    private Integer totalStudyTime;

    @Schema(description = "누적 자리 비움 시간", example = "5654")
    private Integer totalAwayTime;

    @Schema(description = "누적 출석일 수", example = "123")
    private Integer totalAttendanceDays;

    @Schema(description = "연속 출석일 수", example = "23")
    private Integer continueAttendanceDays;

    @Schema(description = "마지막 출석일", example = "2025-08-01")
    private LocalDate lastAttendanceDate;

    public static UserStudyStatsResponse from(UserStudyStat userStudyStat) {

        return UserStudyStatsResponse.builder()
                .userId(userStudyStat.getUser().getId())
                .nickname(userStudyStat.getUser().getNickname())
                .totalStudyTime(userStudyStat.getTotalStudyTime())
                .totalAwayTime(userStudyStat.getTotalAwayTime())
                .totalAttendanceDays(userStudyStat.getTotalAttendanceDays())
                .continueAttendanceDays(userStudyStat.getContinueAttendanceDays())
                .lastAttendanceDate(userStudyStat.getLastAttendanceDate())
                .build();
    }


}
