package org.livestudy.websocket.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorPayload {

    @NotBlank
    private String code;

    @NotBlank
    private String message;

}
