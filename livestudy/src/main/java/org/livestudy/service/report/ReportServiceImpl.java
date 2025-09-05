package org.livestudy.service.report;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.livestudy.domain.report.*;
import org.livestudy.domain.studyroom.Chat;
import org.livestudy.domain.studyroom.StudyRoom;
import org.livestudy.domain.studyroom.StudyRoomStatus;
import org.livestudy.domain.user.User;
import org.livestudy.domain.user.UserStatus;
import org.livestudy.dto.report.ReportDto;
import org.livestudy.exception.CustomException;
import org.livestudy.exception.ErrorCode;
import org.livestudy.repository.ChatRepository;
import org.livestudy.repository.StudyRoomRepository;
import org.livestudy.repository.UserRepository;
import org.livestudy.repository.redis.RoomRedisRepository;
import org.livestudy.repository.report.ReportRepository;
import org.livestudy.repository.report.RestrictionRepository;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private final RoomRedisRepository redisRepo;

    @Transactional
    @Override
    public void report(ReportDto reportDto, Long reporterId) throws JsonProcessingException {
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
        String roomCountStr = redisRepo.getRoomCount(room.getId().toString());
        int participantCount = roomCountStr != null ? Integer.parseInt(roomCountStr) : room.getParticipantsNumber();

        int threshold = calcThreshold(participantCount);
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

    private void kickAndRestrict(StudyRoom room, User target, String reason) throws JsonProcessingException {
        log.debug("[kickAndRestrict] 시작, roomId={}, targetId={}, reason={}", room.getId(), target.getId(), reason);

        // 1️⃣ 제한 메시지 발송
        sendRestrictMessage(target.getId(), reason);

        // 2️⃣ 제한 기록 저장 및 상태 변경
        saveRestriction(room, target, reason);

        // 3️⃣ 시스템 메시지 발송
        sendSystemKickMessage(room.getId(), target.getId(), reason);

        // 4️⃣ 세션 강제 만료
        disconnectKickUser(target.getId());

        // 5️⃣ Redis 방 카운트 감소
        try {
            redisRepo.decrementRoomCount(room.getId().toString());
            log.debug("[kickAndRestrict] Redis 방 카운트 감소 완료, roomId={}, targetId={}", room.getId(), target.getId());
        } catch (DataAccessResourceFailureException ex) {
            log.warn("[kickAndRestrict] Redis 방 카운트 감소 실패 - roomId={}, targetId={}, message={}",
                    room.getId(), target.getId(), ex.getMessage());
        }

        // 6️⃣ DB 방 참가자 수 감소
        room.decrementParticipantsNumber();
        if (room.getParticipantsNumber() < room.getCapacity() && room.getParticipantsNumber() > 0) {
            room.updateStatus(StudyRoomStatus.OPEN);
        }
        log.debug("[kickAndRestrict] DB 방 인원 감소 완료, roomId={}, participantsNumber={}",
                room.getId(), room.getParticipantsNumber());

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

    private void sendSystemKickMessage(Long roomId, Long targetId, String reason) throws JsonProcessingException {
        Map<String, Object> payload = new HashMap<>();
        payload.put("event", "USER_KICKED");
        payload.put("roomId", roomId);
        payload.put("targetId", targetId);
        payload.put("reason", reason);
        payload.put("timestamp", LocalDateTime.now().toString());

        String jsonMessage = new ObjectMapper().writeValueAsString(payload);
        redisTemplate.convertAndSend("systemMessage:" + roomId, jsonMessage);

        log.debug("[sendSystemKickMessage] Redis 발송 완료, roomId={}, payload={}", roomId, jsonMessage);
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
