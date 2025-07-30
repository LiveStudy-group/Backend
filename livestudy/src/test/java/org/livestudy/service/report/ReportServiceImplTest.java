package org.livestudy.service.report;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.livestudy.domain.report.*;
import org.livestudy.domain.studyroom.Chat;
import org.livestudy.domain.studyroom.StudyRoom;
import org.livestudy.domain.user.User;
import org.livestudy.dto.report.ReportDto;
import org.livestudy.exception.CustomException;
import org.livestudy.exception.ErrorCode;
import org.livestudy.repository.ChatRepository;
import org.livestudy.repository.StudyRoomRepository;
import org.livestudy.repository.UserRepository;
import org.livestudy.repository.report.ReportRepository;
import org.livestudy.repository.report.RestrictionRepository;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.session.SessionRegistry;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportServiceImplTest {

    private ReportServiceImpl reportService;

    private ReportRepository reportRepo = mock(ReportRepository.class);
    private RestrictionRepository restrictionRepo = mock(RestrictionRepository.class);
    private StudyRoomRepository roomRepo = mock(StudyRoomRepository.class);
    private ChatRepository chatRepo = mock(ChatRepository.class);
    private UserRepository userRepo = mock(UserRepository.class);
    private StringRedisTemplate redisTemplate = mock(StringRedisTemplate.class);
    private SessionRegistry sessionRegistry = mock(SessionRegistry.class);

    @BeforeEach
    void setUp() {
        reportService = new ReportServiceImpl(
                reportRepo, restrictionRepo, roomRepo, chatRepo,
                userRepo, redisTemplate, sessionRegistry
        );
    }

    @Test
    void report_정상_신고_처리_테스트() {
        // given
        Long reporterId = 1L;
        Long reportedId = 2L;
        Long roomId = 100L;
        Long chatId = 10L;

        StudyRoom room = mock(StudyRoom.class);
        when(room.getId()).thenReturn(roomId);
        when(room.getParticipantsNumber()).thenReturn(5);

        Chat chat = mock(Chat.class);
        User reporter = mock(User.class);
        when(reporter.getId()).thenReturn(reporterId);

        User reported = mock(User.class);
        when(reported.getId()).thenReturn(reportedId);

        ReportDto dto = ReportDto.builder()
                .roomId(roomId)
                .chatId(chatId)
                .reportedId(reportedId)
                .reason(ReportReason.OBSCENE_CONTENT)
                .description("나쁜 행동")
                .build();

        when(roomRepo.getReferenceById(roomId)).thenReturn(room);
        when(chatRepo.getReferenceById(chatId)).thenReturn(chat);
        when(userRepo.getReferenceById(reporterId)).thenReturn(reporter);
        when(userRepo.getReferenceById(reportedId)).thenReturn(reported);

        when(reportRepo.existsByStudyRoomAndChatAndReporterAndReportedAndReason(
                room, chat, reporter, reported, dto.getReason())).thenReturn(false);

        when(reportRepo.countDistinctReporter(room, reported, dto.getReason())).thenReturn(2L); // threshold 넘김

        // when
        reportService.report(dto, reporterId);

        // then
        verify(reportRepo, times(1)).save(any(Report.class));
        verify(restrictionRepo, times(1)).save(any(Restriction.class));
        verify(redisTemplate, atLeastOnce()).convertAndSend(contains("restriction"), anyString());
        verify(userRepo, times(1)).save(any(User.class));
    }

    @Test
    void report_자기자신_신고시_예외() {
        Long userId = 1L;

        ReportDto dto = ReportDto.builder()
                .roomId(1L)
                .reportedId(userId)
                .reason(ReportReason.ABUSE)
                .build();

        User user = mock(User.class);
        when(user.getId()).thenReturn(userId);

        when(userRepo.getReferenceById(userId)).thenReturn(user);

        StudyRoom room = mock(StudyRoom.class);
        when(room.getId()).thenReturn(1L);
        when(room.getParticipantsNumber()).thenReturn(5);
        when(roomRepo.getReferenceById(1L)).thenReturn(room);

        CustomException e = assertThrows(CustomException.class, () -> {
            reportService.report(dto, userId);
        });

        assertEquals(ErrorCode.CANNOT_REPORT_SELF, e.getErrorCode());
    }

    @Test
    void report_중복신고_예외() {
        Long reporterId = 1L;
        Long reportedId = 2L;

        ReportDto dto = ReportDto.builder()
                .roomId(1L)
                .reportedId(reportedId)
                .reason(ReportReason.DISTURBANCE)
                .build();

        StudyRoom room = mock(StudyRoom.class);
        when(room.getId()).thenReturn(1L);

        User reporter = mock(User.class);
        when(reporter.getId()).thenReturn(reporterId);

        User reported = mock(User.class);
        when(reported.getId()).thenReturn(reportedId);

        when(roomRepo.getReferenceById(1L)).thenReturn(room);
        when(userRepo.getReferenceById(reporterId)).thenReturn(reporter);
        when(userRepo.getReferenceById(reportedId)).thenReturn(reported);
        when(reportRepo.existsByStudyRoomAndChatAndReporterAndReportedAndReason(
                any(), any(), eq(reporter), eq(reported), eq(dto.getReason())))
                .thenReturn(true);

        CustomException e = assertThrows(CustomException.class, () -> {
            reportService.report(dto, reporterId);
        });

        assertEquals(ErrorCode.DUPLICATE_REPORT, e.getErrorCode());
    }
}
