package org.livestudy.websocket.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PresenceService {

    private final Map<String, Set<String>> rooms = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> banned = new ConcurrentHashMap<>();


    // 입장
    public boolean join(String roomId, String userId) {

        if (banned.getOrDefault(roomId, Set.of()).contains(userId)) {
            return false;
        }
        rooms.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet())
                .add(userId);
        return true;
    }

    // 퇴장
    public void exit(String roomId, String userId, boolean isBan) {

        rooms.computeIfPresent(roomId, (k, v) -> {
            v.remove(userId);
            return v.isEmpty() ? null : v;
        });

        if (isBan) {
            banned.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(userId);
        }
    }

    // 접속자 목록
    public Set<String> list(String roomId) {
        return rooms.getOrDefault(roomId, Set.of());
    }


}
