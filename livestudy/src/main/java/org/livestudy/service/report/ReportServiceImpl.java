//package org.livestudy.service.report;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.livestudy.domain.report.*;
//import org.livestudy.domain.studyroom.Chat;
//import org.livestudy.domain.studyroom.StudyRoom;
//import org.livestudy.domain.user.User;
//import org.livestudy.domain.user.UserStatus;
//import org.livestudy.dto.report.ReportDto;
//import org.livestudy.exception.CustomException;
//import org.livestudy.exception.ErrorCode;
//import org.livestudy.repository.ChatRepository;
//import org.livestudy.repository.StudyRoomRepository;
//import org.livestudy.repository.UserRepository;
//import org.livestudy.repository.report.ReportRepository;
//import org.livestudy.repository.report.RestrictionRepository;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.security.core.session.SessionInformation;
//import org.springframework.security.core.session.SessionRegistry;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Objects;
//
//@Service
//@Slf4j
//@RequiredArgsConstructor
//public class ReportServiceImpl implements ReportService {
//
//    private final ReportRepository reportRepo;
//    private final RestrictionRepository restrictionRepo;
//    private final StudyRoomRepository roomRepo;
//    private final ChatRepository chatRepo;
//    private final UserRepository userRepo;
//    private final StringRedisTemplate redisTemplate;
//    private final SessionRegistry sessionRegistry;
//
//    @Transactional
//    @Override
//    public void report(ReportDto reportDto, Long reporterId) {
//
//        StudyRoom room = roomRepo.getReferenceById(reportDto.getRoomId());
//        Chat chat = reportDto.getChatId() != null ?
//                chatRepo.getReferenceById(reportDto.getChatId()) : null;
//        User reporter = userRepo.getReferenceById(reporterId);
//        User reported = userRepo.getReferenceById(reportDto.getReportedId());
//
//        if (reporter.equals(reported)) {
//            throw new CustomException(ErrorCode.CANNOT_REPORT_SELF);
//        }
//        // 중복 방지
//        if (reportRepo.existsByStudyRoomAndChatAndReporterAndReportedAndReason(
//                room, chat, reporter, reported, reportDto.getReason())) {
//            throw new CustomException(ErrorCode.DUPLICATE_REPORT);
//        }
//
//        // 저장
//        reportRepo.save(Report.of(
//                room, chat, reporter, reported, reportDto.getReason(), reportDto.getDescription()
//        ));
//
//        // 신고자 수 집계
//        long distinctCnt = reportRepo.countDistinctReporter(room, reported, reportDto.getReason());
//        int threshold = calcThreshold(room.getParticipantsNumber());
//
//        log.debug("room={}, reported={}, distinct={}, threshold={}",
//                room.getId(), reported.getId(), distinctCnt, threshold);
//
//        if (distinctCnt >= threshold) {
//            kickAndRestrict(room, reported, reportDto.getReason().toString());
//        }
//
//    }
//
//    private int calcThreshold(int cnt) {
//        if (cnt <= 10) return 2;
//        return 3;
//    }
//
//
//
//    private void kickAndRestrict(StudyRoom room, User target, String reason) {
//        sendRestrictMessage(target.getId(), reason);
//        saveRestriction(room, target, reason);
//        sendSystemKickMessage(room.getId(), target.getId(), reason);
//        DisconnectKickUser(target.getId());
//    }
//
//    private void sendRestrictMessage(Long targetId, String reason) {
//
//        String restrictionMessageForUser = String.format("'%s' 사유로 신고되어 이용이 제한되었습니다.", reason);
//        redisTemplate.convertAndSend("restriction:" + targetId, restrictionMessageForUser);    }
//
//    private void saveRestriction(StudyRoom room, User target, String reason) {
//
//        String restrictionReason = String.format(
//                "'%s' 사유로 신고되어 이용이 제한되었습니다.",
//                reason);
//
//        restrictionRepo.save(Restriction.of(
//                target, room, null,
//                RestrictionType.TEMPORARY,
//                RestrictionSource.REPORT,
//                restrictionReason,
//                LocalDateTime.now(),
//                LocalDateTime.now().plusHours(24)
//        ));
//
//        target.setUserStatus(UserStatus.TEMPORARY_BAN);
//        userRepo.save(target);
//    }
//
//    private void sendSystemKickMessage(Long roomId, Long targetId, String reason) {
//
//        redisTemplate.convertAndSend("systemMessage:" + roomId,
//                String.format("'%s' 사용자가 '%s' 사유로 신고를 당해 퇴장되었습니다.",
//                        targetId, reason));
//
//    }
//
//    private void DisconnectKickUser(Long userId) {
//        List<Object> principals = sessionRegistry.getAllPrincipals();
//
//        for (Object principal: principals) {
//            if (principal instanceof String &&
//                    Objects.equals(principal, userId.toString())) {
//                List<SessionInformation> sessions = sessionRegistry.getAllSessions(principal, false);
//                for (SessionInformation session: sessions) {
//                    session.expireNow();
//                }
//            }
//        }
//    }
//}
