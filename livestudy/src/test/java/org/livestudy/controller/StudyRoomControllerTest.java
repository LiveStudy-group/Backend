package org.livestudy.controller;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.livestudy.service.livekit.LiveKitTokenService;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class StudyRoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LiveKitTokenService liveKitTokenService;

    @Test
    @DisplayName("LiveKit 토큰 유효 -> 200 OK")
    void validateLiveKitToken_valid() throws Exception {
        Mockito.when(liveKitTokenService.validateToken(anyString())).thenReturn(true);

        mockMvc.perform(get("/api/study-rooms/ws/validate")
                        .param("access_token", "valid_token_123")
                        .header("Authorization", "Bearer 실제_유효한_JWT_토큰"))
                .andExpect(status().isOk());

    }
}
