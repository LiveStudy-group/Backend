package org.livestudy.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.livestudy.dto.UserTitleResponse;
import org.livestudy.service.TitleService;
import org.livestudy.service.UserService;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")  // 테스트용 설정이 있다면 지정
public class TitleGrantControllerTest {

    @MockitoBean
    private RedisMessageListenerContainer redisMessageListenerContainer;


    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TitleService titleService;

    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("대표 칭호 설정 성공")
    @WithMockUser(username = "testUser")
    void equipTitle_success() throws Exception {
        UserTitleResponse mockResponse = UserTitleResponse.builder()
                .titleId(5L)
                .name("타이틀 수집가")
                .description("7개 이상의 타이틀을 수집한 사용자에게 주어지는 칭호")
                .isRepresentative(true)
                .build();

        Mockito.when(titleService.equipTitle(anyLong(), anyLong()))
                .thenReturn(mockResponse);

        mockMvc.perform(post("/api/titles/1/equip")
                        .param("titleId", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titleId").value(5))
                .andExpect(jsonPath("$.name").value("타이틀 수집가"))
                .andExpect(jsonPath("$.isRepresentative").value(true));
    }
}
