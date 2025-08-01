package org.livestudy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.livestudy.domain.data.RoomHistory;
import org.livestudy.domain.studyroom.StudyRoom;
import org.livestudy.domain.studyroom.StudyRoomStatus;
import org.livestudy.domain.title.ConditionType;
import org.livestudy.domain.title.Title;
import org.livestudy.domain.title.TitleCode;
import org.livestudy.domain.user.SocialProvider;
import org.livestudy.domain.user.User;
import org.livestudy.domain.user.statusdata.UserActivity;
import org.livestudy.repository.StudyRoomRepository;
import org.livestudy.repository.TitleRepository;
import org.livestudy.repository.UserRepository;
import org.livestudy.repository.UserTitleRepository;
import org.livestudy.repository.factory.RoomHistoryRepository;
import org.livestudy.service.TitleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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

    private User testUser;

    @BeforeEach
    void setup() {
        userTitleRepository.deleteAll();
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
}
