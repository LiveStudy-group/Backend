package org.livestudy.domain.data;


import jakarta.persistence.*;
import lombok.*;
import org.livestudy.domain.studyroom.StudyRoom;
import org.livestudy.domain.user.User;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_room_id", nullable = false)
    private StudyRoom studyRoom;

    private LocalDateTime joinedAt;

    private LocalDateTime leftAt;

    public void leave(LocalDateTime leftTime) {
        this.leftAt = leftTime;
    }

    public static RoomHistory join(User user, StudyRoom studyRoom) {
        return join(user, studyRoom, LocalDateTime.now());
    }

    public static RoomHistory join(User user, StudyRoom studyRoom, LocalDateTime joinedAt) {
        return RoomHistory.builder()
                .user(user)
                .studyRoom(studyRoom)
                .joinedAt(joinedAt)
                .build();
    }
}
