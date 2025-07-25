package org.livestudy.dto.report;

import lombok.Getter;
import org.livestudy.domain.report.ReportReason;

@Getter
public class ReportRequest {

    private Long roomId;
    private Long chatId;
    private Long reportedId;
    private ReportReason reason;
    private String description;

    public ReportDto toCommand() {
        return new ReportDto(
                roomId, chatId, reportedId, reason, description
        );
    }
}
