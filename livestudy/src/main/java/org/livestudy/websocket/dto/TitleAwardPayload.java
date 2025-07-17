package org.livestudy.websocket.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TitleAwardPayload {

    @NotBlank
    private String userId;

    @NotBlank
    private String username;

    @NotBlank
    private String titleId;

    @NotBlank
    private String titleName;



}
