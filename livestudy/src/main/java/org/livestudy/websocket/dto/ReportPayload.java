package org.livestudy.websocket.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportPayload {

    @NotBlank
    private String reporterId;

    @NotBlank
    private String reporterName;

    @NotBlank
    private String reportedId;

    @NotBlank
    private String reportedName;

    @NotBlank
    private String reason;

    @NotBlank
    private String roomId;
}
