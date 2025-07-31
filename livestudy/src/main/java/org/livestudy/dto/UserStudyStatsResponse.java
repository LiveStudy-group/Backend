package org.livestudy.dto;

import lombok.Builder;
import lombok.Getter;
import org.livestudy.domain.user.statusdata.UserStudyStat;

import java.time.LocalDate;

@Getter
@Builder
public class UserStudyStatsResponse {

    private Long userId;
    private String nickname;
    private Integer totalStudyTime;
    private Integer totalAwayTime;
    private Integer totalAttendanceDays;
    private Integer continueAttendanceDays;
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
