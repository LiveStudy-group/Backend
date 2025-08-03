package org.livestudy.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.livestudy.domain.user.statusdata.DailyStudyRecord;
import org.livestudy.domain.user.User;
import org.livestudy.domain.user.statusdata.UserStudyStat;
import org.livestudy.dto.DailyRecordResponse;
import org.livestudy.dto.UserStudyStatsResponse;
import org.livestudy.exception.CustomException;
import org.livestudy.exception.ErrorCode;
import org.livestudy.repository.DailyStudyRecordRepository;
import org.livestudy.repository.UserStudyStatRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections; // Collections.emptyList()를 사용하기 위함
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.data.Offset.offset;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserStudyStatServiceTest {

    @Mock
    private UserStudyStatRepository userStudyStatRepo;

    @Mock
    private DailyStudyRecordRepository dailyStudyRecordRepo;

    @InjectMocks
    private UserStudyStatServiceImpl userStudyStatService;

    private User testUser;
    private LocalDate fixedToday; // 테스트 날짜
    private LocalDate fixedSixDaysAgo; // 테스트 날짜 6일 전(오늘 포함 7일)


    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .nickname("testUser")
                .build();
        fixedToday = LocalDate.of(2025, 7, 29); // 오늘을 2025년 7월 29일
        fixedSixDaysAgo = fixedToday.minusDays(6); // 2025년 7월 23일
    }

    @Test
    @DisplayName("유저의 공부 통계 조회 성공")
    void getUserStudyStats_success() {
        // Given
        UserStudyStat mockUserStudyStat = UserStudyStat.builder()
                .user(testUser)
                .totalStudyTime(36000)
                .totalAwayTime(3600)
                .totalAttendanceDays(10)
                .continueAttendanceDays(5)
                .lastAttendanceDate(LocalDate.of(2025, 7, 28))
                .build();

        when(userStudyStatRepo.findByUserId(testUser.getId())).thenReturn(Optional.of(mockUserStudyStat));

        // When
        UserStudyStatsResponse response = userStudyStatService.getUserStudyStats(testUser.getId());

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(testUser.getId());
        assertThat(response.getNickname()).isEqualTo(testUser.getNickname());
        assertThat(response.getTotalStudyTime()).isEqualTo(36000);
        assertThat(response.getTotalAwayTime()).isEqualTo(3600);
        assertThat(response.getTotalAttendanceDays()).isEqualTo(10);
        assertThat(response.getContinueAttendanceDays()).isEqualTo(5);
        assertThat(response.getLastAttendanceDate()).isEqualTo(LocalDate.of(2025, 7, 28));
    }

    @Test
    @DisplayName("유저의 공부 통계 조회 실패")
    void getUserStudyStats_notFound() {
        // Given
        when(userStudyStatRepo.findByUserId(any(Long.class))).thenReturn(Optional.empty());

        // When & Then
        CustomException exception = assertThrows(CustomException.class,
                () -> userStudyStatService.getUserStudyStats(999L));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.STAT_NOT_FOUND);
    }

    @Test
    @DisplayName("DailyRecord 조회 성공")
    void getDailyRecord_success() {
        // Given
        LocalDate startDate = LocalDate.of(2025, 7, 26);
        LocalDate endDate = LocalDate.of(2025, 7, 28);

        DailyStudyRecord record1 = DailyStudyRecord.builder()
                .id(1L).user(testUser).recordDate(LocalDate.of(2025, 7, 26))
                .dailyStudyTime(3600).dailyAwayTime(600).build();
        DailyStudyRecord record2 = DailyStudyRecord.builder()
                .id(2L).user(testUser).recordDate(LocalDate.of(2025, 7, 27))
                .dailyStudyTime(7200).dailyAwayTime(1800).build();
        DailyStudyRecord record3 = DailyStudyRecord.builder()
                .id(3L).user(testUser).recordDate(LocalDate.of(2025, 7, 28))
                .dailyStudyTime(1800).dailyAwayTime(200).build();

        when(dailyStudyRecordRepo.findByUserIdAndRecordDateBetweenOrderByRecordDateAsc(
                testUser.getId(), startDate, endDate))
                .thenReturn(Arrays.asList(record1, record2, record3));

        // When
        List<DailyRecordResponse> responses = userStudyStatService.getDailyRecord(testUser.getId(), startDate, endDate);

        // Then
        assertThat(responses).isNotNull();
        assertThat(responses).hasSize(3);

        assertThat(responses.get(0).getRecordDate()).isEqualTo(LocalDate.of(2025, 7, 26));
        assertThat(responses.get(0).getDailyStudyTime()).isEqualTo(3600);
        assertThat(responses.get(0).getDailyAwayTime()).isEqualTo(600);
        assertThat(responses.get(0).getFocusRatio()).isCloseTo(85.71, offset(0.01));

        assertThat(responses.get(1).getRecordDate()).isEqualTo(LocalDate.of(2025, 7, 27));
        assertThat(responses.get(1).getDailyStudyTime()).isEqualTo(7200);
        assertThat(responses.get(1).getDailyAwayTime()).isEqualTo(1800);
        assertThat(responses.get(1).getFocusRatio()).isCloseTo(80.00, offset(0.01));

        assertThat(responses.get(2).getRecordDate()).isEqualTo(LocalDate.of(2025, 7, 28));
        assertThat(responses.get(2).getDailyStudyTime()).isEqualTo(1800);
        assertThat(responses.get(2).getDailyAwayTime()).isEqualTo(200);
        assertThat(responses.get(2).getFocusRatio()).isCloseTo(90.00, offset(0.01));
    }

    @Test
    @DisplayName("일별 기록이 없는 경우 빈 리스트 조회")
    void getDailyRecord_noRecords() {
        // Given
        LocalDate startDate = LocalDate.of(2025, 7, 1);
        LocalDate endDate = LocalDate.of(2025, 7, 7);
        when(dailyStudyRecordRepo.findByUserIdAndRecordDateBetweenOrderByRecordDateAsc(
                testUser.getId(), startDate, endDate))
                .thenReturn(Collections.emptyList());

        // When
        List<DailyRecordResponse> responses = userStudyStatService.getDailyRecord(testUser.getId(), startDate, endDate);

        // Then
        assertThat(responses).isNotNull();
        assertThat(responses).isEmpty();
    }

    @Test
    @DisplayName("날짜 파라미터가 없는 경우 7일간의 데이터 조회")
    void getDailyRecord_withDefaultDates() {
        // Given
        DailyStudyRecord record1 = DailyStudyRecord.builder()
                .id(10L).user(testUser).recordDate(fixedSixDaysAgo)
                .dailyStudyTime(1000).dailyAwayTime(100).build();
        DailyStudyRecord record2 = DailyStudyRecord.builder()
                .id(11L).user(testUser).recordDate(fixedToday)
                .dailyStudyTime(2000).dailyAwayTime(200).build();

        when(dailyStudyRecordRepo.findByUserIdAndRecordDateBetweenOrderByRecordDateAsc(
                testUser.getId(), fixedSixDaysAgo, fixedToday))
                .thenReturn(Arrays.asList(record1, record2));

        // When
        List<DailyRecordResponse> responses = userStudyStatService.getDailyRecord(testUser.getId(), fixedSixDaysAgo, fixedToday);

        // Then
        assertThat(responses).isNotNull();
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getRecordDate()).isEqualTo(fixedSixDaysAgo);
        assertThat(responses.get(1).getRecordDate()).isEqualTo(fixedToday);
    }

    @Test
    @DisplayName("AverageStudyRatio 조회 성공")
    void getAverageStudyRatio_success() {
        // Given
        LocalDate startDate = LocalDate.of(2025, 7, 26);
        LocalDate endDate = LocalDate.of(2025, 7, 28);

        DailyStudyRecord record1 = DailyStudyRecord.builder()
                .id(1L).user(testUser).recordDate(LocalDate.of(2025, 7, 26))
                .dailyStudyTime(3600).dailyAwayTime(600).build();
        DailyStudyRecord record2 = DailyStudyRecord.builder()
                .id(2L).user(testUser).recordDate(LocalDate.of(2025, 7, 27))
                .dailyStudyTime(7200).dailyAwayTime(1800).build();
        DailyStudyRecord record3 = DailyStudyRecord.builder()
                .id(3L).user(testUser).recordDate(LocalDate.of(2025, 7, 28))
                .dailyStudyTime(1800).dailyAwayTime(200).build();
        DailyStudyRecord record4_zero_time = DailyStudyRecord.builder()
                .id(4L).user(testUser).recordDate(LocalDate.of(2025, 7, 25))
                .dailyStudyTime(0).dailyAwayTime(0).build();

        when(dailyStudyRecordRepo.findByUserIdAndRecordDateBetweenOrderByRecordDateAsc(
                testUser.getId(), startDate, endDate))
                .thenReturn(Arrays.asList(record1, record2, record3, record4_zero_time));

        // When
        Double averageRatio = userStudyStatService.getAverageStudyRatio(testUser.getId(), startDate, endDate);

        // Then
        assertThat(averageRatio).isCloseTo(82.89, offset(0.01));
    }

    @Test
    @DisplayName("기록이 없어 AverageStudyRatio 0.0 조회")
    void getAverageStudyRatio_noRecords() {
        // Given
        LocalDate startDate = LocalDate.of(2025, 7, 1);
        LocalDate endDate = LocalDate.of(2025, 7, 7);
        when(dailyStudyRecordRepo.findByUserIdAndRecordDateBetweenOrderByRecordDateAsc(
                testUser.getId(), startDate, endDate))
                .thenReturn(Collections.emptyList());

        // When
        Double averageRatio = userStudyStatService.getAverageStudyRatio(testUser.getId(), startDate, endDate);

        // Then
        assertThat(averageRatio).isEqualTo(0.0);
    }

    @Test
    @DisplayName("공부, 자리 비움 시간이 0일 경우 AverageStudyRatio 조회")
    void getAverageStudyRatio_zeroTotalTime() {
        // Given
        LocalDate startDate = LocalDate.of(2025, 7, 1);
        LocalDate endDate = LocalDate.of(2025, 7, 1);
        DailyStudyRecord record = DailyStudyRecord.builder()
                .id(1L).user(testUser).recordDate(LocalDate.of(2025, 7, 1))
                .dailyStudyTime(0).dailyAwayTime(0).build();

        when(dailyStudyRecordRepo.findByUserIdAndRecordDateBetweenOrderByRecordDateAsc(
                testUser.getId(), startDate, endDate))
                .thenReturn(Arrays.asList(record));

        // When
        Double averageRatio = userStudyStatService.getAverageStudyRatio(testUser.getId(), startDate, endDate);

        // Then
        assertThat(averageRatio).isEqualTo(0.0);
    }

    @Test
    @DisplayName("날짜 파라미터가 없는 경우 7일 간의 AverageStudyRatio 조회")
    void getAverageStudyRatio_withDefaultDates() {
        // Given
        DailyStudyRecord record1 = DailyStudyRecord.builder()
                .id(12L).user(testUser).recordDate(fixedSixDaysAgo)
                .dailyStudyTime(1000).dailyAwayTime(100).build(); // 1000/1100 = 90.90%
        DailyStudyRecord record2 = DailyStudyRecord.builder()
                .id(13L).user(testUser).recordDate(fixedToday.minusDays(3))
                .dailyStudyTime(500).dailyAwayTime(500).build();  // 500/1000 = 50.00%
        DailyStudyRecord record3 = DailyStudyRecord.builder()
                .id(14L).user(testUser).recordDate(fixedToday)
                .dailyStudyTime(2000).dailyAwayTime(0).build();   // 2000/2000 = 100.00%

        when(dailyStudyRecordRepo.findByUserIdAndRecordDateBetweenOrderByRecordDateAsc(
                testUser.getId(), fixedSixDaysAgo, fixedToday))
                .thenReturn(Arrays.asList(record1, record2, record3));

        // When
        Double averageRatio = userStudyStatService.getAverageStudyRatio(testUser.getId(), fixedSixDaysAgo, fixedToday);

        // Then
        // 총 학습 시간 = 1000 + 500 + 2000 = 3500
        // 총 전체 시간 = 1100 + 1000 + 2000 = 4100
        // 평균 학습률 = (3500 / 4100) * 100 = 85.36...
        assertThat(averageRatio).isCloseTo(85.4, offset(0.01)); // 반올림(Math.round)
    }
}