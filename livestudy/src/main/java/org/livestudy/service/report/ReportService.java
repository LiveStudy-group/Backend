package org.livestudy.service.report;

import org.livestudy.dto.report.ReportDto;

public interface ReportService {

    // 신고 저장
    void report(ReportDto reportDto, Long reporterId);
}

