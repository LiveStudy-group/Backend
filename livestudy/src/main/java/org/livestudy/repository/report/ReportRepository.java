package org.livestudy.repository.report;

import org.livestudy.domain.report.Report;
import org.livestudy.domain.report.ReportReason;
import org.livestudy.domain.studyroom.Chat;
import org.livestudy.domain.studyroom.StudyRoom;
import org.livestudy.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ReportRepository extends JpaRepository<Report, Long> {

    boolean existsByStudyRoomAndChatAndReporterAndReportedAndReason(
            StudyRoom room, Chat chat, User reporter, User reported, ReportReason reason);

    @Query("""
            SELECT COUNT(DISTINCT r.reporter.id)
            FROM Report r
            WHERE r.studyRoom = :room
            AND r.reported   = :reported
            AND r.reason     = :reason
           """)
    long countDistinctReporter(@Param("room")StudyRoom room,
                               @Param("reported")User reported,
                               @Param("reason")ReportReason reason);
}
