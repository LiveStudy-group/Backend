package org.livestudy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.livestudy.domain.title.ConditionType;
import org.livestudy.domain.title.Title;
import org.livestudy.domain.title.TitleCode;
import org.livestudy.domain.user.SocialProvider;
import org.livestudy.domain.user.User;
import org.livestudy.domain.user.UserActivity;
import org.livestudy.repository.TitleRepository;
import org.livestudy.repository.UserRepository;
import org.livestudy.repository.UserTitleRepository;
import org.livestudy.service.TitleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TitleServiceIntegrationTest {

    @Autowired private TitleService titleService;
    @Autowired private UserRepository userRepository;
    @Autowired private TitleRepository titleRepository;
    @Autowired private UserTitleRepository userTitleRepository;

    @BeforeEach
    void setUp() {
        /// 칭호가 DB에 없다면 저장
        for (TitleCode code : TitleCode.values()) {
            if (!titleRepository.findByCode(code).isPresent()) {
                Title title = Title.builder()
                        .code(code)
                        .name(code.name())
                        .conditionType(ConditionType.FIRST_ROOM_ENTER) // 적절히 설정
                        .conditionValue(1)
                        .description("테스트용 칭호")
                        .build();
                titleRepository.save(title);
            }
        }
    }

    @BeforeEach
    void cleanUp() {
        userRepository.deleteByEmail("test@example.com");
    }


    @Test
    void when_you_get_first_enterAnyRoom_youGetTitle() {
        User user = User.builder()
                .email("test@example.com")
                .password("12359843")
                .nickname("user123")
                .socialProvider(SocialProvider.LOCAL)
                .build();

        userRepository.save(user);

        UserActivity activity = new UserActivity(1, 0, 0, 0, 0 , true, 0, null, 0, false, 0);

        List<Title> granted = titleService.evaluateAndGrantTitles(user, activity);

        assertThat(granted)
                .extracting(Title::getCode)
                .contains(TitleCode.FIRST_ROOM_ENTER);
    }
}
