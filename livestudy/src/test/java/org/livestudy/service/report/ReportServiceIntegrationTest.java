package org.livestudy.service.report;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.livestudy.domain.report.Report;
import org.livestudy.domain.report.ReportReason;
import org.livestudy.domain.studyroom.StudyRoom;
import org.livestudy.domain.studyroom.StudyRoomStatus;
import org.livestudy.domain.user.SocialProvider;
import org.livestudy.domain.user.User;
import org.livestudy.domain.user.UserStatus;
import org.livestudy.dto.report.ReportDto;
import org.livestudy.repository.StudyRoomRepository;
import org.livestudy.repository.UserRepository;
import org.livestudy.repository.report.ReportRepository;
import org.livestudy.repository.report.RestrictionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ReportServiceIntegrationTest {

    @Autowired
    private ReportService reportService;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private StudyRoomRepository roomRepo;

    @Autowired
    private ReportRepository reportRepo;

    @Autowired
    private RestrictionRepository restrictionRepo;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private User reporter1;
    private User reporter2;
    private User reported;
    private StudyRoom room;

    @BeforeEach
    void setup() {
        // 사용자 생성
        reporter1 = User.builder().email("reporter1@test.com").nickname("r1").userStatus(UserStatus.NORMAL).build();
        reporter2 = User.builder().email("reporter2@test.com").nickname("r2").userStatus(UserStatus.NORMAL).build();
        reported = User.builder().email("reported@test.com").nickname("target").userStatus(UserStatus.NORMAL).build();

        userRepo.save(reporter1);
        userRepo.save(reporter2);
        userRepo.save(reported);

        // 방 생성
        room = StudyRoom.builder().participantsNumber(4).build();
        roomRepo.save(room);
    }

    @Test
    @Rollback
    void 신고_정상처리_및_저장_검증() {
        // given
        User reporter = userRepo.save(User.builder().email("tester1@example.com").password("123123").userStatus(UserStatus.NORMAL).socialProvider(SocialProvider.LOCAL).nickname("신고자").build());
        User reported = userRepo.save(User.builder().email("tester2@example.com").password("123123").userStatus(UserStatus.NORMAL).socialProvider(SocialProvider.LOCAL).nickname("신고대상").build());

        StudyRoom room = roomRepo.save(StudyRoom.builder().participantsNumber(3).status(StudyRoomStatus.OPEN).build());

        ReportDto dto = ReportDto.builder()
                .roomId(room.getId())
                .reportedId(reported.getId())
                .reason(ReportReason.ABUSE)
                .description("욕설")
                .build();

        // when
        reportService.report(dto, reporter.getId());

        // then
        Report saved = reportRepo.findAll().get(0);
        assertThat(saved.getReporter().getId()).isEqualTo(reporter.getId());
        assertThat(saved.getReported().getId()).isEqualTo(reported.getId());
        assertThat(saved.getReason()).isEqualTo(ReportReason.ABUSE);
    }

    @Test
    @Rollback
    void 중복신고_시_예외발생() {
        // given
        User reporter = userRepo.save(User.builder()
                .email("tester1@example.com")
                .password("123123")
                .userStatus(UserStatus.NORMAL)
                .socialProvider(SocialProvider.LOCAL)
                .nickname("신고자")
                .build());

        User reported = userRepo.save(User.builder()
                .email("tester2@example.com")
                .password("123123")
                .userStatus(UserStatus.NORMAL)
                .socialProvider(SocialProvider.LOCAL)
                .nickname("신고대상")
                .build());

        StudyRoom room = roomRepo.save(StudyRoom.builder()
                .participantsNumber(3)
                .status(StudyRoomStatus.OPEN)
                .build());

        ReportDto dto = ReportDto.builder()
                .roomId(room.getId())
                .reportedId(reported.getId())
                .reason(ReportReason.ABUSE)
                .description("욕설")
                .build();

        // 첫 신고
        reportService.report(dto, reporter.getId());

        // when & then - 중복 신고 시 CustomException(DUPLICATE_REPORT) 발생 확인
        org.junit.jupiter.api.Assertions.assertThrows(
                org.livestudy.exception.CustomException.class,
                () -> reportService.report(dto, reporter.getId())
        );
      
    void test_threshold_초과_제재_Status_변화_4명입장중인방에서() {
        // 첫 번째 신고
        ReportDto dto1 = ReportDto.builder()
                .roomId(room.getId())
                .reportedId(reported.getId())
                .reason(ReportReason.DISTURBANCE)
                .build();
        reportService.report(dto1, reporter1.getId());

        // 두 번째 신고 -> threshold 도달
        ReportDto dto2 = ReportDto.builder()
                .roomId(room.getId())
                .reportedId(reported.getId())
                .reason(ReportReason.DISTURBANCE)
                .build();
        reportService.report(dto2, reporter2.getId());

        // 신고 저장 확인
        long count = reportRepo.countDistinctReporter(room, reported, ReportReason.DISTURBANCE);
        assertThat(count).isEqualTo(2);

        // 제재 적용 확인
        User updatedReported = userRepo.getReferenceById(reported.getId());
        assertThat(updatedReported.getUserStatus()).isEqualTo(UserStatus.TEMPORARY_BAN);

        // Redis 메시지 확인
        String message = redisTemplate.opsForList().rightPop("restriction:" + reported.getId());
        assertThat(message).contains("DISTURBANCE");
    }
}
