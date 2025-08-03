package org.livestudy.component;

import lombok.RequiredArgsConstructor;
import org.livestudy.domain.user.statusdata.DailyStudyRecord;
import org.livestudy.domain.user.statusdata.UserStudyStat;
import org.livestudy.repository.*;
import org.livestudy.repository.factory.RoomHistoryRepository;

import org.livestudy.repository.factory.UserLoginHistoryRepository;
import org.livestudy.repository.report.ReportRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class UserActivityFactory {

    private final UserRepository userRepo;
    private final DailyStudyRecordRepository dailyStudyRecordRepo;
    private final UserStudyStatRepository userStudyStatRepo;
    private final UserLoginHistoryRepository loginHistoryRepo;
    private final ChatRepository chatRepo;
    private final ReportRepository reportRepo;
    private final UserTitleRepository titleRepo;
    private final RoomHistoryRepository roomHistoryRepo;


    // ✅ 1. 특정 시간에 로그인했는지 확인 (오전 9시 이전)
    public boolean hasLoggedInAt9Hour(Long userId, LocalDate date, int hour){
        return loginHistoryRepo.existsLoginAtHour(userId, date, hour);
    }

    // ✅ 2. 마지막 로그인 시간 확인 (ex. 밤 10시 이후 조건)
    public Optional<LocalTime> getLastLoginTime(Long userId){
        return loginHistoryRepo.findLastLoginTime(userId);
    }

    // ✅ 3. 특정 기간 동안 공부 기록 확인
    public List<DailyStudyRecord> getDailyStudyRecords(Long userId, LocalDate from, LocalDate to){
        return dailyStudyRecordRepo.findByUserIdAndRecordDateBetweenOrderByRecordDateAsc(userId, from, to);
    }

    // ✅ 4. 채팅 메시지 총 횟수
    public long getTotalChatCount(Long userId) {
        return chatRepo.countByParticipant_User_Id(userId);
    }

    // ✅ 5. 내가 신고한 횟수
    public long getReportCountAsReporter(Long userId) {
        return reportRepo.countByReporterId(userId);
    }

    // ✅ 6. 첫 입장 여부 확인
    public boolean hasEnteredAnyRoom(Long userId){
        return roomHistoryRepo.existsByUserId(userId);
    }

    // ✅ 7. 일일 집중 시간 가져오기(분단위)
    public int getOneDayFocusMinutes(Long userId) {
        return dailyStudyRecordRepo.findByUserIdAndRecordDate(userId, LocalDate.now())
                .map(DailyStudyRecord::getDailyStudyTime)
                .orElse(0);
    }

    // ✅ 8. 연속으로 1시간 이상 집중한 일수 가져오기
    public int getRunConsecutiveFocusHour(Long userId) {
        return userStudyStatRepo.findByUserId(userId)
                .map(UserStudyStat::getConsecutiveFocusDaysOverHour) // ✅ 바른 대상
                .orElse(0);
    }

    // ✅ 9. 연속 로그인 일수 가져오기
    public int getConsecutiveLoginDays(Long userId) {
        return userStudyStatRepo.findByUserId(userId)
                .map(UserStudyStat::getContinueAttendanceDays)
                .orElse(0);
    }

    // ✅ 10. 획득한 칭호 갯수 가져오기
    public int getEarnedTitleCount(Long userId) {
        return userStudyStatRepo.findByUserId(userId)
                .map(UserStudyStat::getTitleCount)
                .orElse(0);
    }

    // ✅ 11. 누적 공부 시간 가져오기
    public int getTotalStudyTime(Long userId) {
        return userStudyStatRepo.findByUserId(userId)
                .map(UserStudyStat::getTotalStudyTime)
                .orElse(0);
    }
}
