package org.livestudy.domain.user.statusdata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@Builder
@AllArgsConstructor
public class UserActivity {

    private final int consecutiveFocusDays; // 연속 집중 일수
    private final int oneHourFocusStreak;   // 50분 이상 집중 연속 횟수
    private final int totalChatCount;       // 누적 채팅 횟수
    private final boolean enteredFirstRoom; // 첫 입장 여부
    private final int reportCount;          // 받은 신고 수
    private final LocalTime lastLoginTime;  // 마지막 로그인 시간
    private final int earnedTitleCount;     // 칭호 획득 수
    private final boolean loggedInAt9AmToday; // 오늘 9시에 로그인했는지 여부
    private final int consecutiveFocusDaysOverHour; // 1시간 이상 집중한 날짜
}
