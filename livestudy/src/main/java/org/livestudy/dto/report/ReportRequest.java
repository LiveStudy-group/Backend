package org.livestudy.dto.report;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.livestudy.domain.report.ReportReason;

@Getter
@Schema(description = "신고 요청 정보 DTO")
public class ReportRequest {

    @Schema(description = "신고가 발생한 스터디룸 ID", example = "123", nullable = true)
    private Long roomId;

    @Schema(description = "신고 당한 채팅 ID", example = "345")
    private Long chatId;

    @Schema(description = "신고 당한 유저 ID", example = "56723")
    private Long reportedId;

    @Schema(description = "신고 사유", example = "ABUSE")
    private ReportReason reason;

    @Schema(description = "신고 사유 상세 설명", example = "해당 유저가 욕설을 하였습니다.", nullable = true)
    private String description;

    public ReportDto toCommand() {
        return new ReportDto(
                roomId, chatId, reportedId, reason, description
        );
    }
}