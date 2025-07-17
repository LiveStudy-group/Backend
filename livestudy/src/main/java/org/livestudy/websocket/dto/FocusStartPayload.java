package org.livestudy.websocket.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FocusStartPayload {

    @NotBlank
    private String userId;

    @NotBlank
    private String roomId;

    @NotBlank
    private String userName;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private java.time.Instant startTime;
}
