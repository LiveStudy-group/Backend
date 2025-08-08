package org.livestudy.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.livestudy.domain.studyroom.FocusStatus;
import org.livestudy.domain.studyroom.StudyRoomParticipant;
import org.livestudy.domain.user.statusdata.DailyStudyRecord;
import org.livestudy.domain.user.User;
import org.livestudy.domain.user.statusdata.UserStudyStat;
import org.livestudy.dto.timer.TimerResponse;
import org.livestudy.dto.timer.TimerStatusResponse;
import org.livestudy.exception.CustomException;
import org.livestudy.exception.ErrorCode;
import org.livestudy.repository.DailyStudyRecordRepository;
import org.livestudy.repository.StudyRoomParticipantRepository;
import org.livestudy.repository.UserStudyStatRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TimerServiceImpl implements TimerService {

    private final StudyRoomParticipantRepository participantRepo;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserStudyStatRepository userStudyStatRepo;
    private final DailyStudyRecordRepository dailyStudyRecordRepo;

    @Override
    public TimerResponse startFocus(Long userId, Long roomId) {
        log.info("집중 시작 요청: userId={}, roomId={}", userId, roomId);

        StudyRoomParticipant participant = findActiveParticipant(userId, roomId);
        LocalDateTime now = LocalDateTime.now();
        int awayTime = 0;

        // 현재 상태가 자리비움이었다면 자리비움 시간을 누적
        if (participant.getFocusStatus() == FocusStatus.AWAY && participant.getStatusChangedAt() != null) {
            int awayDuration = (int) Duration.between(participant.getStatusChangedAt(), now).getSeconds();
            participant = updateParticipant(participant, FocusStatus.FOCUS, now,
                    participant.getStudyTime(), participant.getAwayTime() + awayDuration);
            awayTime = awayDuration;
        } else {
            participant = updateParticipant(participant, FocusStatus.FOCUS, now,
                    participant.getStudyTime(), participant.getAwayTime());
        }

        StudyRoomParticipant saved = participantRepo.save(participant);

        // 유저의 공부 정보에 업데이트
        if (awayTime > 0) {
            updateStudyStatsAndDailyRecord(saved.getUser(), 0, awayTime);
        }

        // WebSocket으로 상태 변경 알림
        broadcastTimerUpdate(roomId, userId, "FOCUS_START", saved);

        return buildTimerResponse(saved);
    }

    @Override
    public TimerResponse pauseFocus(Long userId, Long roomId) {
        log.info("집중 일시정지 요청: userId={}, roomId={}", userId, roomId);

        StudyRoomParticipant participant = findActiveParticipant(userId, roomId);
        LocalDateTime now = LocalDateTime.now();
        int studyTime = 0;

        // 현재 상태가 집중이었다면 집중 시간을 누적
        if (participant.getFocusStatus() == FocusStatus.FOCUS && participant.getStatusChangedAt() != null) {
            int studyDuration = (int) Duration.between(participant.getStatusChangedAt(), now).getSeconds();
            participant = updateParticipant(participant, FocusStatus.AWAY, now,
                    participant.getStudyTime() + studyDuration, participant.getAwayTime());
            studyTime = studyDuration;
        } else {
            participant = updateParticipant(participant, FocusStatus.AWAY, now,
                    participant.getStudyTime(), participant.getAwayTime());
        }

        StudyRoomParticipant saved = participantRepo.save(participant);

        // 유저의 공부 정보에 업데이트
        if (studyTime > 0) {
            updateStudyStatsAndDailyRecord(saved.getUser(), studyTime, 0);
        }

        // WebSocket으로 상태 변경 알림
        broadcastTimerUpdate(roomId, userId, "FOCUS_PAUSE", saved);

        return buildTimerResponse(saved);
    }

    @Override
    public TimerResponse resumeFocus(Long userId, Long roomId) {
        log.info("집중 재개 요청: userId={}, roomId={}", userId, roomId);
        return startFocus(userId, roomId); // 시작과 동일한 로직
    }

    @Override
    public TimerResponse stopFocus(Long userId, Long roomId) {
        log.info("집중 종료 요청: userId={}, roomId={}", userId, roomId);

        StudyRoomParticipant participant = findActiveParticipant(userId, roomId);
        LocalDateTime now = LocalDateTime.now();
        int studyTime = 0;
        int awayTime = 0;

        // 현재 진행 중인 시간을 마지막으로 누적
        if (participant.getStatusChangedAt() != null) {
            int duration = (int) Duration.between(participant.getStatusChangedAt(), now).getSeconds();

            if (participant.getFocusStatus() == FocusStatus.FOCUS) {
                participant = updateParticipant(participant, FocusStatus.AWAY, now,
                        participant.getStudyTime() + duration, participant.getAwayTime());
                studyTime = duration;
            } else {
                participant = updateParticipant(participant, FocusStatus.AWAY, now,
                        participant.getStudyTime(), participant.getAwayTime() + duration);
                awayTime = duration;
            }
        }

        // 방에서 퇴장 처리
        participant = StudyRoomParticipant.builder()
                .id(participant.getId())
                .user(participant.getUser())
                .studyRoom(participant.getStudyRoom())
                .joinTime(participant.getJoinTime())
                .leaveTime(now)
                .focusStatus(participant.getFocusStatus())
                .statusChangedAt(participant.getStatusChangedAt())
                .studyTime(participant.getStudyTime())
                .awayTime(participant.getAwayTime())
                .build();

        StudyRoomParticipant saved = participantRepo.save(participant);

        // 유저의 공부 정보에 업데이트
        if (studyTime > 0 || awayTime > 0) {
            updateStudyStatsAndDailyRecord(saved.getUser(), studyTime, awayTime);
        }

        // WebSocket으로 종료 알림
        broadcastTimerUpdate(roomId, userId, "FOCUS_STOP", saved);

        return buildTimerResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public TimerStatusResponse getTimerStatus(Long userId, Long roomId) {
        StudyRoomParticipant participant = findActiveParticipant(userId, roomId);
        LocalDateTime now = LocalDateTime.now();

        // 현재 상태 지속 시간 계산
        int currentStatusDuration = 0;
        int currentSessionStudyTime = participant.getStudyTime();
        int currentSessionAwayTime = participant.getAwayTime();

        if (participant.getStatusChangedAt() != null) {
            currentStatusDuration = (int) Duration.between(participant.getStatusChangedAt(), now).getSeconds();

            // 현재 진행 중인 시간을 임시로 계산 (DB에는 저장하지 않음)
            if (participant.getFocusStatus() == FocusStatus.FOCUS) {
                currentSessionStudyTime += currentStatusDuration;
            } else {
                currentSessionAwayTime += currentStatusDuration;
            }
        }

        return TimerStatusResponse.builder()
                .userId(userId)
                .roomId(roomId)
                .nickname(participant.getUser().getNickname())
                .currentStatus(participant.getFocusStatus())
                .currentSessionStudyTime(currentSessionStudyTime)
                .currentSessionAwayTime(currentSessionAwayTime)
                .totalStudyTime(participant.getStudyTime())
                .totalAwayTime(participant.getAwayTime())
                .statusChangedAt(participant.getStatusChangedAt())
                .currentStatusDuration(currentStatusDuration)
                .build();
    }

    // === Private Helper Methods ===

    private StudyRoomParticipant findActiveParticipant(Long userId, Long roomId) {
        return participantRepo.findByUserIdAndStudyRoomIdAndLeaveTimeIsNull(userId, roomId)
                .orElseThrow(() -> {
                    log.error("활성 참여자를 찾을 수 없음: userId={}, roomId={}", userId, roomId);
                    return new CustomException(ErrorCode.USER_NOT_FOUND);
                });
    }

    private StudyRoomParticipant updateParticipant(StudyRoomParticipant original,
                                                   FocusStatus newStatus,
                                                   LocalDateTime statusChangedAt,
                                                   Integer studyTime,
                                                   Integer awayTime) {
        return StudyRoomParticipant.builder()
                .id(original.getId())
                .user(original.getUser())
                .studyRoom(original.getStudyRoom())
                .joinTime(original.getJoinTime())
                .leaveTime(original.getLeaveTime())
                .focusStatus(newStatus)
                .statusChangedAt(statusChangedAt)
                .studyTime(studyTime)
                .awayTime(awayTime)
                .build();
    }

    private void updateStudyStatsAndDailyRecord(User user, int studyTime, int awayTime) {

        // UserStudyStat 업데이트
        UserStudyStat userStudyStat = userStudyStatRepo.findByUserId(user.getId())
                .orElseGet(() -> {
                    log.info("userId: {} 유저의 새로운 UserStudyStats 생성", user.getId());
                    return UserStudyStat.builder()
                            .user(user)
                            .totalStudyTime(0)
                            .totalAwayTime(0)
                            .totalAttendanceDays(0)
                            .continueAttendanceDays(0)
                            .build();
                });

        userStudyStat.setTotalStudyTime(userStudyStat.getTotalStudyTime() + studyTime);
        userStudyStat.setTotalAwayTime(userStudyStat.getTotalAwayTime() + awayTime);

        // 출석일 업데이트
        if (studyTime > 0) {
            updateAttendance(userStudyStat);
        }

        userStudyStatRepo.save(userStudyStat);
        log.info("UserStudyStat 업데이트 완료");

        // DailyStudyRecord 업데이트
        LocalDate today = LocalDate.now();
        DailyStudyRecord dailyStudyRecord = dailyStudyRecordRepo
                .findByUser_IdAndRecordDate(user.getId(), today)
                .orElseGet(() -> {
                    log.info("userId: {} 유저의 오늘({}) DailyStudyRecord 생성", user.getId(), today);
                    return DailyStudyRecord.builder()
                            .user(user)
                            .recordDate(today)
                            .dailyStudyTime(0)
                            .dailyAwayTime(0)
                            .build();
                });
        dailyStudyRecord.setDailyStudyTime(dailyStudyRecord.getDailyStudyTime() + studyTime);
        dailyStudyRecord.setDailyAwayTime(dailyStudyRecord.getDailyAwayTime() + awayTime);

        dailyStudyRecordRepo.save(dailyStudyRecord);
        log.info("DailyStudyRecord 업데이트 완료");
    }

    private void updateAttendance(UserStudyStat stat) {
        LocalDate today = LocalDate.now();
        LocalDate lastDate = stat.getLastAttendanceDate();

        // 처음 출석하는 경우
        if (lastDate == null || !lastDate.isEqual(today)) {
            stat.setTotalAttendanceDays(stat.getTotalAttendanceDays() + 1);

            // 연속 출석인지 확인
            if (lastDate != null && lastDate.plusDays(1).isEqual(today)) {
                stat.setContinueAttendanceDays(stat.getContinueAttendanceDays() +1);
            } else {
                stat.setContinueAttendanceDays(1);
            }

            stat.setLastAttendanceDate(today);
        }
    }

    private TimerResponse buildTimerResponse(StudyRoomParticipant participant) {
        return TimerResponse.builder()
                .userId(participant.getUser().getId())
                .roomId(participant.getStudyRoom().getId())
                .status(participant.getFocusStatus())
                .totalStudyTime(participant.getStudyTime())
                .totalAwayTime(participant.getAwayTime())
                .statusChangedAt(participant.getStatusChangedAt())
                .joinTime(participant.getJoinTime())
                .build();
    }

    private void broadcastTimerUpdate(Long roomId, Long userId, String action, StudyRoomParticipant participant) {
        Map<String, Object> message = Map.of(
                "type", "TIMER_UPDATE",
                "userId", userId,
                "action", action,
                "status", participant.getFocusStatus().name(),
                "totalStudyTime", participant.getStudyTime(),
                "totalAwayTime", participant.getAwayTime(),
                "timestamp", LocalDateTime.now()
        );

        messagingTemplate.convertAndSend("/topic/" + roomId, message);
        log.info("타이머 상태 변경 알림 전송: roomId={}, userId={}, action={}", roomId, userId, action);
    }
}