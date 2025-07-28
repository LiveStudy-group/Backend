package org.livestudy.websocket.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FocusUpdatedPayload {

    @NotBlank
    private String userId;

    @NotBlank
    private String roomId;

    @NotBlank
    private String nickname;

    @Min(0)
    private int focusedCount;

}
