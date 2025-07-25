package org.livestudy.service.report;

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
import org.livestudy.service.report.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
}
