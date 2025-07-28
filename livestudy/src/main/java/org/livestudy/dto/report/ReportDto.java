package org.livestudy.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.livestudy.domain.report.ReportReason;

@Getter
@Builder
@AllArgsConstructor
public class ReportDto {

    private final Long roomId;
    private final Long chatId;
    private final Long reportedId;
    private final ReportReason reason;
    private final String description;
}
