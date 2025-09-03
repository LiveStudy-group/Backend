package org.livestudy.service.report;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.livestudy.domain.report.*;
import org.livestudy.domain.studyroom.Chat;
import org.livestudy.domain.studyroom.StudyRoom;
import org.livestudy.domain.user.User;
import org.livestudy.domain.user.UserStatus;
import org.livestudy.dto.report.ReportDto;
import org.livestudy.exception.CustomException;
import org.livestudy.exception.ErrorCode;
import org.livestudy.repository.ChatRepository;
import org.livestudy.repository.StudyRoomRepository;
import org.livestudy.repository.UserRepository;
import org.livestudy.repository.report.ReportRepository;
import org.livestudy.repository.report.RestrictionRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepo;
    private final RestrictionRepository restrictionRepo;
    private final StudyRoomRepository roomRepo;
    private final ChatRepository chatRepo;
    private final UserRepository userRepo;
    private final StringRedisTemplate redisTemplate;
    private final SessionRegistry sessionRegistry;

    @Transactional
    @Override
    public void report(ReportDto reportDto, Long reporterId) {
        log.debug("[report] 호출됨, reportDto={}, reporterId={}", reportDto, reporterId);

        StudyRoom room = roomRepo.getReferenceById(reportDto.getRoomId());
        Chat chat = reportDto.getChatId() != null ? chatRepo.getReferenceById(reportDto.getChatId()) : null;
        User reporter = userRepo.getReferenceById(reporterId);
        User reported = userRepo.getReferenceById(reportDto.getReportedId());

        log.debug("[report] room={}, chat={}, reporter={}, reported={}",
                room.getId(), chat != null ? chat.getId() : null, reporter.getId(), reported.getId());

        if (reporter.equals(reported)) {
            log.debug("[report] 자기 자신 신고 시도됨, 예외 발생");
            throw new CustomException(ErrorCode.CANNOT_REPORT_SELF);
        }

        boolean exists = reportRepo.existsByStudyRoomAndChatAndReporterAndReportedAndReason(
                room, chat, reporter, reported, reportDto.getReason());
        log.debug("[report] 중복 신고 여부={}", exists);
        if (exists) {
            throw new CustomException(ErrorCode.DUPLICATE_REPORT);
        }

        reportRepo.save(Report.of(room, chat, reporter, reported, reportDto.getReason(), reportDto.getDescription()));
        log.debug("[report] 신고 저장 완료");

        long distinctCnt = reportRepo.countDistinctReporter(room, reported, reportDto.getReason());
        int threshold = calcThreshold(room.getParticipantsNumber());
        log.debug("[report] 신고자 수={}, 임계치={}, 조건 충족={}", distinctCnt, threshold, distinctCnt >= threshold);

        if (distinctCnt >= threshold) {
            String displayReason = reportDto.getReason().toString() + " 등의 사유";
            log.debug("[report] kickAndRestrict 호출 준비, reason={}", displayReason);
            kickAndRestrict(room, reported, displayReason);
        } else {
            log.debug("[report] kickAndRestrict 조건 미충족, 종료");
        }
    }

    private int calcThreshold(int cnt) {
        if (cnt <= 10) return 2;
        return 3;
    }

    private void kickAndRestrict(StudyRoom room, User target, String reason) {
        log.debug("[kickAndRestrict] 시작, roomId={}, targetId={}, reason={}", room.getId(), target.getId(), reason);

        sendRestrictMessage(target.getId(), reason);
        saveRestriction(room, target, reason);
        sendSystemKickMessage(room.getId(), target.getId(), reason);
        disconnectKickUser(target.getId());

        log.debug("[kickAndRestrict] 완료, targetId={}", target.getId());
    }

    private void sendRestrictMessage(Long targetId, String reason) {
        String restrictionMessage = String.format("'%s' 사유로 신고되어 이용이 제한되었습니다.", reason);
        redisTemplate.convertAndSend("restriction:" + targetId, restrictionMessage);
        log.debug("[sendRestrictMessage] Redis 발송 완료, targetId={}, message={}", targetId, restrictionMessage);
    }

    private void saveRestriction(StudyRoom room, User target, String reason) {
        log.debug("[saveRestriction] 시작, targetId={}", target.getId());

        String restrictionReason = String.format("'%s' 사유로 신고되어 이용이 제한되었습니다.", reason);

        restrictionRepo.save(Restriction.of(
                target, room, null,
                RestrictionType.TEMPORARY,
                RestrictionSource.REPORT,
                restrictionReason,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(24)
        ));

        target.setUserStatus(UserStatus.TEMPORARY_BAN);
        userRepo.save(target);

        log.debug("[saveRestriction] 완료, targetId={}", target.getId());
    }

    private void sendSystemKickMessage(Long roomId, Long targetId, String reason) {
        String systemMessage = String.format("'%s' 사용자가 '%s' 사유로 신고를 당해 퇴장되었습니다.", targetId, reason);
        redisTemplate.convertAndSend("systemMessage:" + roomId, systemMessage);
        log.debug("[sendSystemKickMessage] Redis 발송 완료, roomId={}, targetId={}, message={}", roomId, targetId, systemMessage);
    }

    private void disconnectKickUser(Long userId) {
        log.debug("[disconnectKickUser] 시작, userId={}", userId);

        List<Object> principals = sessionRegistry.getAllPrincipals();
        log.debug("[disconnectKickUser] 총 principals 수={}", principals.size());

        for (Object principal : principals) {
            log.debug("[disconnectKickUser] principal class={}, value={}", principal.getClass(), principal);
            if (principal instanceof String && Objects.equals(principal, userId.toString())) {
                List<SessionInformation> sessions = sessionRegistry.getAllSessions(principal, false);
                log.debug("[disconnectKickUser] 해당 principal 세션 수={}", sessions.size());
                for (SessionInformation session : sessions) {
                    session.expireNow();
                    log.debug("[disconnectKickUser] 세션 만료 처리 완료, sessionId={}", session.getSessionId());
                }
            }
        }

        log.debug("[disconnectKickUser] 완료, userId={}", userId);
    }
}
