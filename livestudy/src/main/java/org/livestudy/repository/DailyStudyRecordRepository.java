package org.livestudy.repository;

import org.livestudy.domain.user.DailyStudyRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyStudyRecordRepository extends JpaRepository<DailyStudyRecord, Long> {

    Optional<DailyStudyRecord> findByUserIdAndRecordDate (Long userId, LocalDate recordDate);

    // 특정 기간의 DailyStudyRecord 조회(날짜 순)
    List<DailyStudyRecord> findByUserIdAndRecordDateBetweenOrderByRecordDateAsc(
            Long userId, LocalDate startDate, LocalDate endDate);

    @Query("""
           SELECT d.dailyStudyTime
           FROM DailyStudyRecord d
           WHERE d.userId = :userId
             AND d.recordDate = :today
           """)
    Optional<Integer> findTodayStudyTime(@Param("userId") Long userId,
                                         @Param("today") LocalDate today);

}
