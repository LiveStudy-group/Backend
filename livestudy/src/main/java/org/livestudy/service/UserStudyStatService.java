package org.livestudy.service;

import org.livestudy.dto.DailyRecordResponse;
import org.livestudy.dto.TodayStudyTimeResponse;
import org.livestudy.dto.UserStudyStatsResponse;

import java.time.LocalDate;
import java.util.List;

public interface UserStudyStatService {

    UserStudyStatsResponse getUserStudyStats(Long userId);

    List<DailyRecordResponse> getDailyRecord(Long userId, LocalDate startDate, LocalDate endDate);

    Double getAverageStudyRatio(Long userId, LocalDate startDate, LocalDate endDate);

    TodayStudyTimeResponse getTodayStudyTime(Long userId);
}
