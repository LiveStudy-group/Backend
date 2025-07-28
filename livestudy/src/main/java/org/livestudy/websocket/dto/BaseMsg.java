package org.livestudy.websocket.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class BaseMsg<T> {

    @NotNull
    private MsgType type;

    @Valid
    private T payload;

    @NotNull
    private Instant timeStamp;
}
