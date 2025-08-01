package org.livestudy.repository.factory;

import org.livestudy.domain.report.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserReportRepository extends JpaRepository<Report, Long> {

    // userId가 신고한 횟수
    long countByReporterId(Long reporterId);

}
