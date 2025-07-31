package org.livestudy.domain.data;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RoomHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String roomId;

    private LocalDateTime joinedAt;

    private LocalDateTime leftAt;

    public void leave(LocalDateTime leftTime) {
        this.leftAt = leftTime;
    }

    public static RoomHistory join(Long userId, String roomId, LocalDateTime joinedAt) {
        return RoomHistory.builder()
                .userId(userId)
                .roomId(roomId)
                .joinedAt(joinedAt)
                .build();
    }
}
