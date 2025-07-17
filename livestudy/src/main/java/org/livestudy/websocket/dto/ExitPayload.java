package org.livestudy.websocket.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExitPayload {

    @NotBlank
    private String userId;

    @NotBlank
    private String roomId;

    private boolean isBanned;

    @NotBlank
    private String userName;
}
