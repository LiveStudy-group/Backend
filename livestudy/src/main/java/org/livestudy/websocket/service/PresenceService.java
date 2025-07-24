package org.livestudy.websocket.service;

import lombok.RequiredArgsConstructor;
import org.livestudy.domain.user.User;
import org.livestudy.domain.user.UserStatus;
import org.livestudy.exception.CustomException;
import org.livestudy.exception.ErrorCode;
import org.livestudy.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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
            throw new CustomException(ErrorCode.USER_NOT_IN_ROOM);
        }

        participants.remove(userId);

        if (participants.isEmpty()) {
            rooms.remove(roomId);
        }

    }

    // 접속자 목록
    public Set<String> list(String roomId) {
        return rooms.getOrDefault(roomId, Set.of());
    }


}
