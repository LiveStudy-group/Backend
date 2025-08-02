package org.livestudy.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.livestudy.domain.user.DailyStudyRecord;
import org.livestudy.domain.user.UserStudyStat;
import org.livestudy.dto.DailyRecordResponse;
import org.livestudy.dto.UserStudyStatsResponse;
import org.livestudy.exception.CustomException;
import org.livestudy.exception.ErrorCode;
import org.livestudy.repository.DailyStudyRecordRepository;
import org.livestudy.repository.UserStudyStatRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserStudyStatServiceImpl implements UserStudyStatService{

    private final UserStudyStatRepository userStudyStatRepo;
    private final DailyStudyRecordRepository dailyStudyRecordRepo;

    @Override
    public UserStudyStatsResponse getUserStudyStats(Long userId) {

        UserStudyStat userStudyStat = userStudyStatRepo.findByUserId(userId)
                .orElseGet(() -> {
                    log.error("userId: {} 유저의 공부 기록 통계를 찾을 수 없습니다. ", userId);
                    throw new CustomException(ErrorCode.STAT_NOT_FOUND);
                });

        return UserStudyStatsResponse.from(userStudyStat);
    }

    @Override
    public List<DailyRecordResponse> getDailyRecord(Long userId, LocalDate startDate, LocalDate endDate) {

        List<DailyStudyRecord> records = dailyStudyRecordRepo
                .findByUserIdAndRecordDateBetweenOrderByRecordDateAsc(
                        userId, startDate, endDate
                );

        List<DailyRecordResponse> responses = records.stream()
                .map(record -> DailyRecordResponse.builder()
                        .recordDate(record.getRecordDate())
                        .dailyStudyTime(Long.valueOf(record.getDailyStudyTime()))
                        .dailyAwayTime(Long.valueOf(record.getDailyAwayTime()))
                        .focusRatio(record.getFocusRatio())
                        .build())
                .toList();

        if (responses.isEmpty()) {
            log.warn("userId: {} 유저의 {} 부터 {} 까지의 일별 집중도 기록이 없습니다.",
                    userId, startDate, endDate);
        }

        return responses;
    }

    @Override
    public Double getAverageStudyRatio(Long userId, LocalDate startDate, LocalDate endDate) {

        List<DailyStudyRecord> records = dailyStudyRecordRepo
                .findByUserIdAndRecordDateBetweenOrderByRecordDateAsc(
                        userId, startDate, endDate
                );

        if (records.isEmpty()) {
            log.warn("userId: {} 유저의 {} 부터 {} 까지의 기록이 없어 평균 집중률을 계산할 수 없습니다.",
                    userId, startDate, endDate);
            return 0.0;
        }

        double totalStudyTime = 0.0;
        double totalTime = 0.0;

        for (DailyStudyRecord dailyStudyRecord: records) {
            int dailyStudy = dailyStudyRecord.getDailyStudyTime();
            int dailyAway = dailyStudyRecord.getDailyAwayTime();
            int dailyTotal = dailyStudy + dailyAway;

            if (dailyTotal > 0) {
                totalStudyTime += dailyStudy;
                totalTime += dailyTotal;
            }
        }

        if (totalTime == 0) {
            return 0.0;
        }

        return Math.round((totalStudyTime / totalTime) * 1000.0) / 10.0;
    }
}
