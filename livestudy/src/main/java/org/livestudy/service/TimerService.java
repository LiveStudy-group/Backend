package org.livestudy.service;

import org.livestudy.dto.timer.TimerResponse;
import org.livestudy.dto.timer.TimerStatusResponse;

public interface TimerService {

    /**
     * 집중 타이머 시작
     */
    TimerResponse startFocus(Long userId, Long roomId);

    /**
     * 집중 타이머 일시정지 (자리비움)
     */
    TimerResponse pauseFocus(Long userId, Long roomId);

    /**
     * 집중 타이머 재개
     */
    TimerResponse resumeFocus(Long userId, Long roomId);

    /**
     * 집중 타이머 종료
     */
    TimerResponse stopFocus(Long userId, Long roomId);

    /**
     * 현재 타이머 상태 조회
     */
    TimerStatusResponse getTimerStatus(Long userId, Long roomId);
}