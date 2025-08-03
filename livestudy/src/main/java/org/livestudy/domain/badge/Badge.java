package org.livestudy.domain.badge;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.livestudy.domain.title.TitleCategory;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum Badge {
    // 🏁 입장/기본
    NONE("칭호 없음", "기본값", TitleCategory.SYSTEM),
    FIRST_ROOM_ENTER("첫 입장", "처음 방에 입장했을 때 취득", TitleCategory.SYSTEM),
    ANYTIME_LOGIN("일일 학습러", "언제든 접속하면 취득", TitleCategory.ATTENDANCE),

    // 🌙 시간/습관 관련
    NIGHT_OWL("야행성", "밤 10시 이후 30회 이상 접속", TitleCategory.ATTENDANCE),
    CLEAN_HUNTER("청결 헌터", "신고 5회 이상 시 취득", TitleCategory.SYSTEM),
    FROM_9_START("From 9 Start", "매일 오전 9시 정각에 접속, 7일 이상 진행 시 취득", TitleCategory.ATTENDANCE),
    SEVEN_DAYS("개근상", "7일 연속 출석", TitleCategory.ATTENDANCE),
    THIRTY_DAYS("습관 만드는 길", "30일 이상 연속 출석", TitleCategory.ATTENDANCE),

    // ⏱ 집중 관련
    FOCUS_BEGINNER("Focus Beginner", "하루 30분 이상 집중 1회", TitleCategory.FOCUS),
    FOCUS_RUNNER("Focus Runner", "하루 1시간 이상 집중, 3일 연속", TitleCategory.FOCUS),
    FOCUS_MASTER("Focus 좀 치는데?!", "50분 이상 타이머를 연속 5회 달성 시 취득", TitleCategory.FOCUS),
    FIRST_ONE_HOUR("첫 걸음도 한 시간부터!", "누적 집중 1시간 이상", TitleCategory.FOCUS),
    HUNDRED_FOCUS("Hundred Focus", "누적 집중 100시간 달성", TitleCategory.FOCUS),
    TEN_HOURS_ONE_DAY("Only Running for day", "하루에 10시간 이상 집중했을 시 취득", TitleCategory.FOCUS),

    // 💬 참여/채팅
    CHATTER("Wide Foot", "채팅 100회 이상", TitleCategory.CHAT),

    // 📖 수집
    TITLE_COLLECTOR("칭호 수첩", "칭호 7개 이상 획득", TitleCategory.SPECIAL);

    private final String displayName;
    private final String description;
    private final TitleCategory titleCategory;

    Badge(String displayName, String description, TitleCategory titleCategory) {
        this.displayName = displayName;
        this.description = description;
        this.titleCategory = titleCategory;
    }
}
