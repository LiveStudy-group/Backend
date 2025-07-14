package org.livestudy.domain.admin;

import jakarta.persistence.*;
import lombok.*;
import org.livestudy.domain.BaseEntity;
import org.livestudy.domain.report.Restriction;
import org.livestudy.domain.report.RestrictionSource;
import org.livestudy.domain.report.RestrictionType;
import org.livestudy.domain.studyroom.StudyRoom;
import org.livestudy.domain.user.User;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Admin extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long    id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reported_id", nullable = false)
    private User reported;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_room_id")
    private StudyRoom studyRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private Admin admin; // 확인 요청 필요

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RestrictionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RestrictionSource source;

    @Lob
    @Column(nullable = false)
    private String reason;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    public static Restriction of(User reported,
                                 StudyRoom studyRoom,
                                 Admin admin,
                                 RestrictionType type,
                                 RestrictionSource source,
                                 String reason,
                                 LocalDateTime startedAt,
                                 LocalDateTime endedAt){

        return Restriction.builder()
                .reported(reported)
                .studyRoom(studyRoom)
                .admin(admin)
                .type(type)
                .source(source)
                .reason(reason)
                .startedAt(startedAt)
                .endedAt(endedAt)
                .build();
    }
}
