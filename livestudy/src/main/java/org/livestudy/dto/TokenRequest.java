package org.livestudy.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenRequest {

    @Schema(description = "입장할 방의 ID", example = "study-room-1")
    @JsonProperty("roomName") // FE에서 보내는 필드명과 매핑
    private String roomId;


    @Schema(description = "사용자 ID", example = "user_8890")
    @JsonIgnore
    @JsonProperty("identity") // FE에서 보내는 필드명과 매핑
    private String userId;
}
