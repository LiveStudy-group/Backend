package org.livestudy.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.livestudy.dto.EnterStudyRoomResponse;
import org.livestudy.dto.timer.TimerResponse;
import org.livestudy.dto.timer.TimerStartRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TimerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void enterRoomAndStartFocus() throws Exception {
        String testUserId = "33";

        // 1️⃣ 스터디룸 입장
        MvcResult enterResult = mockMvc.perform(post("/api/study-rooms/enter")
                        .param("userId", testUserId))
                .andExpect(status().isOk())
                .andReturn();

        EnterStudyRoomResponse enterResponse = objectMapper.readValue(
                enterResult.getResponse().getContentAsString(),
                EnterStudyRoomResponse.class
        );

        assertThat(enterResponse.roomId()).isNotNull();
        assertThat(enterResponse.accessToken()).isNotNull();

        Long roomId = Long.valueOf(enterResponse.roomId()); // 문자열 → Long 변환

        // 2️⃣ 타이머 시작
        TimerStartRequest startRequest = new TimerStartRequest(Long.valueOf(testUserId), roomId);

        MvcResult timerResult = mockMvc.perform(post("/api/timer/start")
                        .header("Authorization", "Bearer " + enterResponse.accessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(startRequest)))
                .andExpect(status().isOk())
                .andReturn();

        TimerResponse timerResponse = objectMapper.readValue(
                timerResult.getResponse().getContentAsString(),
                TimerResponse.class
        );

        assertThat(timerResponse.getUserId()).isEqualTo(Long.valueOf(testUserId));
        assertThat(timerResponse.getRoomId()).isEqualTo(roomId);
        assertThat(timerResponse.getStatus()).isNotNull();
    }
}
