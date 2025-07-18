package org.livestudy.websocket.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

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
    @jsonformat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant startTime;
}
