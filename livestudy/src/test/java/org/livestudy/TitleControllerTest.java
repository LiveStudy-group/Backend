package org.livestudy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.livestudy.component.UserActivityFactory;
import org.livestudy.domain.data.LoginHistory;
import org.livestudy.domain.data.RoomHistory;
import org.livestudy.domain.studyroom.*;
import org.livestudy.domain.title.ConditionType;
import org.livestudy.domain.title.Title;
import org.livestudy.domain.title.TitleCode;
import org.livestudy.domain.title.UserTitle;
import org.livestudy.domain.user.SocialProvider;
import org.livestudy.domain.user.User;
import org.livestudy.domain.user.statusdata.DailyStudyRecord;
import org.livestudy.domain.user.statusdata.UserStudyStat;
import org.livestudy.repository.*;
import org.livestudy.repository.factory.RoomHistoryRepository;
import org.livestudy.repository.factory.UserChatRepository;
import org.livestudy.repository.factory.UserLoginHistoryRepository;
import org.livestudy.service.TitleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TitleServiceIntegrationTest {

    @Autowired private TitleService titleService;
    @Autowired private UserRepository userRepository;
    @Autowired private TitleRepository titleRepository;
    @Autowired private UserTitleRepository userTitleRepository;
    @Autowired private RoomHistoryRepository roomHistoryRepository;
    @Autowired private StudyRoomRepository  studyRoomRepository;
    @Autowired private StudyRoomParticipantRepository studyRoomParticipantRepository;
    @Autowired private UserChatRepository userChatRepository;
    @Autowired private DailyStudyRecordRepository dailyStudyRecordRepository;
    @Autowired private UserStudyStatRepository userStudyStatRepository;
    @Autowired private UserLoginHistoryRepository userLoginHistoryRepository;

    @Autowired private UserActivityFactory factory;

    @MockitoBean
    private RedisMessageListenerContainer redisMessageListenerContainer;


    private User testUser;

    @BeforeEach
    void setup() {
        userTitleRepository.deleteAll();
        dailyStudyRecordRepository.deleteAll();
        userStudyStatRepository.deleteAll();
        userChatRepository.deleteAll();
        studyRoomParticipantRepository.deleteAll();
        userRepository.deleteByEmail("test@example.com");

        testUser = User.builder()
                .email("test@example.com")
                .password("1234")
                .nickname("titleTester")
                .socialProvider(SocialProvider.LOCAL)
                .build();
        userRepository.save(testUser);

        StudyRoom studyRoom = StudyRoom.builder()
                .id(1L)
                .capacity(20)
                .participantsNumber(0)
                .status(StudyRoomStatus.OPEN)
                .build();

        String roomId = String.valueOf(studyRoom.getId());

        // ✅ 테스트용 TitleCode 값이 없으면 등록
        for (TitleCode code : TitleCode.values()) {
            titleRepository.findByCode(code).orElseGet(() -> {
                Title title = Title.builder()
                        .code(code)
                        .name(code.name())
                        .description("테스트용 칭호")
                        .conditionType(ConditionType.FIRST_ROOM_ENTER)
                        .conditionValue(1)
                        .build();
                return titleRepository.save(title);
            });
        }
    }

    @Test
    void should_grant_title_when_user_enters_first_room() {
        // ✅ 조건 충족을 위한 mock 데이터 등록
        roomHistoryRepository.save(RoomHistory.join(testUser.getId(), "test-room"));

        // ✅ 칭호 판별
        List<Title> granted = titleService.evaluateAndGrantTitles(testUser.getId());

        assertThat(granted)
                .extracting(Title::getCode)
                .contains(TitleCode.FIRST_ROOM_ENTER);
    }

    @Test
    void should_grant_chatter_title_when_chat_count_exceeds_100() {
        StudyRoom room = studyRoomRepository.save(StudyRoom.builder()
                .capacity(20).participantsNumber(0).status(StudyRoomStatus.OPEN).build());

        StudyRoomParticipant participant = studyRoomParticipantRepository.save(
                StudyRoomParticipant.builder()
                        .user(testUser)
                        .focusStatus(FocusStatus.FOCUS)
                        .joinTime(LocalDateTime.now().minusHours(4))
                        .studyTime(180)
                        .awayTime(60)
                        .studyRoom(room)
                        .build()
        );

        for (int i = 0; i < 100; i++) {
            userChatRepository.save(Chat.builder()
                    .studyRoom(room)
                    .participant(participant)
                    .createdAt(LocalDateTime.now().minusHours(4).plusMinutes(i))
                    .message("test message")
                    .build());
        }


        List<Title> granted = titleService.evaluateAndGrantTitles(testUser.getId());

        assertThat(granted)
                .extracting(Title::getCode)
                .contains(TitleCode.CHATTER);
    }

    @Test
    void should_grant_title_collector_when_user_has_7_titles() {
        // 이미 보유한 칭호 6개 등록
        Title t1 = titleRepository.findByCode(TitleCode.FIRST_ROOM_ENTER).orElseThrow();
        Title t2 = titleRepository.findByCode(TitleCode.CHATTER).orElseThrow();
        Title t3 = titleRepository.findByCode(TitleCode.FOCUS_BEGINNER).orElseThrow();
        Title t4 = titleRepository.findByCode(TitleCode.HUNDRED_FOCUS).orElseThrow();
        Title t5 = titleRepository.findByCode(TitleCode.CLEAN_HUNTER).orElseThrow();
        Title t6 = titleRepository.findByCode(TitleCode.FIRST_ONE_HOUR).orElseThrow();
        Title t7 = titleRepository.findByCode(TitleCode.FOCUS_RUNNER).orElseThrow();

        userTitleRepository.save(UserTitle.grant(testUser, t1));
        userTitleRepository.save(UserTitle.grant(testUser, t2));
        userTitleRepository.save(UserTitle.grant(testUser, t3));
        userTitleRepository.save(UserTitle.grant(testUser, t4));
        userTitleRepository.save(UserTitle.grant(testUser, t5));
        userTitleRepository.save(UserTitle.grant(testUser, t6));
        userTitleRepository.save(UserTitle.grant(testUser, t7));

        userStudyStatRepository.save(UserStudyStat.builder()
                .user(testUser)
                .titleCount(7)
                .build());


        List<Title> granted = titleService.evaluateAndGrantTitles(testUser.getId());

        assertThat(granted)
                .extracting(Title::getCode)
                .contains(TitleCode.TITLE_COLLECTOR);
    }
    @Test
    void should_grant_first_one_hour_when_daily_focus_time_exceeds_60_minutes() {
        dailyStudyRecordRepository.save(DailyStudyRecord.builder()
                .user(testUser)
                .recordDate(LocalDate.now())
                .dailyStudyTime(61)
                .dailyAwayTime(0)
                .build());

        userStudyStatRepository.save(UserStudyStat.builder()
                        .user(testUser)
                        .totalStudyTime(61)
                .build());



        List<Title> granted = titleService.evaluateAndGrantTitles(testUser.getId());

        assertThat(granted)
                .extracting(Title::getCode)
                .contains(TitleCode.FIRST_ONE_HOUR);
    }

    @Test
    void should_grant_seven_days_when_user_attended_7_days_in_a_row() {
        LocalDate today = LocalDate.now();
        for (int i = 0; i < 7; i++) {
            dailyStudyRecordRepository.save(DailyStudyRecord.builder()
                    .user(testUser)
                    .recordDate(today.minusDays(i))
                    .dailyStudyTime(30)
                    .dailyAwayTime(0)
                    .build());

        }

        userStudyStatRepository.save(
                UserStudyStat.builder()
                        .user(testUser)
                        .totalStudyTime(210)
                        .totalAwayTime(0)
                        .continueAttendanceDays(7)
                        .build()
        );

        List<Title> granted = titleService.evaluateAndGrantTitles(testUser.getId());

        assertThat(granted).extracting(Title::getCode)
                .contains(TitleCode.SEVEN_DAYS);
    }

    @Test
    void should_grant_from_9_start_when_user_attended_at_9AM_with_7days_in_a_row(){
        LocalDate today = LocalDate.now();
        // 1) 7일 연속 로그인 기록을 UserLoginHistory에 저장 (9시 이전 로그인)
        for (int i = 0; i < 7; i++) {
            userLoginHistoryRepository.save(LoginHistory.builder()
                    .userId(testUser.getId())
                            .loginDate(today.minusDays(i))
                    .loginTime(LocalTime.of( 9, 0)) // 9시 이전 로그인
                    .build());
        }

        for (int i = 0; i < 7; i++) {
            dailyStudyRecordRepository.save(DailyStudyRecord.builder()
                    .user(testUser)
                    .recordDate(today.minusDays(i))
                    .dailyStudyTime(30)
                    .dailyAwayTime(0)
                    .build());
        }

        // 3) 실제 테스트: UserActivityFactory의 hasLoggedInAt9Hour 체크
        boolean hasLoggedInAt9 = factory.hasLoggedInAt9Hour(testUser.getId(), today, 9);
        assertThat(hasLoggedInAt9).isTrue();

        // 4) 그 외 칭호 평가 등도 필요시 추가
        List<Title> granted = titleService.evaluateAndGrantTitles(testUser.getId());
        assertThat(granted).extracting(Title::getCode)
                .contains(TitleCode.FROM_9_START);
    }
}
