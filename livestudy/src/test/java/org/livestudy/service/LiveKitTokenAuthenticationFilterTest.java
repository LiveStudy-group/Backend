package org.livestudy.service;


import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.livestudy.component.LiveKitTokenVerifier;
import org.livestudy.security.jwt.JwtTokenProvider;
import org.livestudy.websocket.security.LiveKitTokenAuthenticationFilter;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LiveKitTokenAuthenticationFilterTest {

    @Mock
    LiveKitTokenVerifier liveKitTokenVerifier;

    @Mock
    JwtTokenProvider jwtTokenProvider;

    @Mock
    FilterChain filterChain;

    @InjectMocks
    LiveKitTokenAuthenticationFilter filter;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }


    @Test
    void givenRtcPathAndValidToken_setsAuthentication() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/rtc/enter");
        request.setParameter("access_token", "valid-token");

        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtTokenProvider.validateToken("valid-token")).thenReturn(true);
        when(jwtTokenProvider.getAuthentication("valid-token")).thenReturn(mock(Authentication.class));

        filter.doFilterInternal(request, response, filterChain);

        verify(jwtTokenProvider).validateToken("valid-token");
        verify(jwtTokenProvider).getAuthentication("valid-token");
        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void givenRtcPathAndInvalidToken_returns401() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/rtc/enter");
        request.setParameter("access_token", "invalid-token");

        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtTokenProvider.validateToken("invalid-token")).thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        assertEquals(401, response.getStatus());
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    void givenNonRtcPath_filterBypasses() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/ws/somepath");

        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
