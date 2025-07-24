package org.livestudy.repository.report;

import org.livestudy.domain.report.Restriction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface RestrictionRepository extends JpaRepository<Restriction, Long> {

    @Query("""
            SELECT r
            FROM Restriction r
            WHERE r.reported.id = :userId
            AND (r.endedAt IS NULL OR r.endedAt > :now)
            """)
    List<Restriction> findActiveByUserId(Long userId, LocalDateTime now);
}
