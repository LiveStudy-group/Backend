package org.livestudy.service.report;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.livestudy.dto.report.ReportDto;

public interface ReportService {

    // 신고 저장
    void report(ReportDto reportDto, Long reporterId) throws JsonProcessingException;
}

