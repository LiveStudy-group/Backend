package org.livestudy.service.livekit;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.livestudy.dto.EnterStudyRoomResponse;
import org.livestudy.service.StudyRoomService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LiveKitJoinService {

    private final StudyRoomService studyRoomService;
    private final LiveKitTokenService liveKitTokenService;

    @Transactional
    public EnterStudyRoomResponse joinRoomAndGetToken(String userId) {
        // 1. Redis + DB에서 방 배정하기
        Long roomId = studyRoomService.enterRoom(userId);

        String token = liveKitTokenService.generateToken(userId, String.valueOf(roomId));

        // 2. Livekit JWT 토큰 발급
        return new EnterStudyRoomResponse(String.valueOf(roomId), token);

    }
}
