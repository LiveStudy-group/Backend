package org.livestudy.domain.report;


import jakarta.persistence.*;
import lombok.*;
import org.livestudy.domain.studyroom.Chat;
import org.livestudy.domain.studyroom.StudyRoom;
import org.livestudy.domain.user.User;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "reports",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_room_chat_reason",
                columnNames = {"study_room_id", "chat_id",
                        "reporter_id", "reported_id", "reason"}))
@EntityListeners(AuditingEntityListener.class)
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "study_room_id", nullable = false)
    private StudyRoom studyRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reported_id", nullable = false)
    private User reported;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ReportReason reason;

    @Column
    private String description;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public static Report of(StudyRoom studyRoom,
                            Chat chat,
                            User reporter,
                            User reported,
                            ReportReason reason,
                            String description) {

        return Report.builder()
                .studyRoom(studyRoom)
                .chat(chat)
                .reporter(reporter)
                .reported(reported)
                .reason(reason)
                .description(description)
                .build();
    }
}
