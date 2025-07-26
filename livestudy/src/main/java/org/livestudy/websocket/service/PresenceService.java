package org.livestudy.websocket.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.livestudy.domain.user.User;
import org.livestudy.domain.user.UserStatus;
import org.livestudy.exception.CustomException;
import org.livestudy.exception.ErrorCode;
import org.livestudy.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class PresenceService {

    private final Map<String, Set<String>> rooms = new ConcurrentHashMap<>();
    private final UserRepository userRepo;

    // 입장
    public void join(String roomId, String userId) {

        // 이용 정지된 유저 검사
        User user = userRepo.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (user.getUserStatus() == UserStatus.TEMPORARY_BAN ||
        user.getUserStatus() == UserStatus.PERMANENT_BAN) {
            log.error("userId: {} 는 이용 정지된 유저입니다", userId);
            throw new CustomException(ErrorCode.USER_SUSPENDED);
        }

        rooms.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet())
                .add(userId);

    }

    // 퇴장
    public void exit(String roomId, String userId) {

        Set<String> participants = rooms.get(roomId);

        // 방이 올바르지 않은 경우 or 이용자가 방에 존재하지 않는 경우
        if (participants == null || !participants.contains(userId)) {
            log.error(" {} 유저가 해당 {} 스터디룸에 존재하지 않습니다.", userId, roomId);
            throw new CustomException(ErrorCode.USER_NOT_IN_ROOM);
        }

        participants.remove(userId);

        // 방이 비어있으면 삭제
        if (participants.isEmpty()) {
            rooms.remove(roomId);
        }

    }

    // 접속자 목록
    public Set<String> list(String roomId) {
        return rooms.getOrDefault(roomId, Set.of());
    }


}
