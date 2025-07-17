package org.livestudy.websocket.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatPayload {

    @NotBlank
    private String userId;

    @NotBlank
    private String roomId;

    @NotBlank
    private String userName;

    @NotBlank
    private String message;

}
