package org.livestudy.domain.studyroom;

import jakarta.persistence.*;
import lombok.*;
import org.livestudy.domain.user.User;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "study_room_participants")
public class StudyRoomParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "study_room_id", nullable = false)
    private StudyRoom studyRoom;

    @Column(name = "join_time", nullable = false)
    private LocalDateTime joinTime;

    @Column(name = "leave_time")
    private LocalDateTime leaveTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "focus_status", nullable = false)
    private FocusStatus focusStatus;

    @Column(name = "status_changed_at")
    private LocalDateTime statusChangedAt;

    @Builder.Default
    @Column(name = "study_time", nullable = false)
    private Integer studyTime = 0;

    @Builder.Default
    @Column(name = "away_time", nullable = false)
    private Integer awayTime = 0;
}
