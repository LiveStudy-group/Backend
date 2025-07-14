package org.livestudy.domain.studyroom;

import jakarta.persistence.*;
import lombok.*;
import org.livestudy.domain.BaseEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StudyRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    @Column(name = "participants_number", nullable = false)
    private Integer participantsNumber = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StudyRoomStatus status;

    public static StudyRoom of(Integer participantsNumber,
                               StudyRoomStatus status) {

        return StudyRoom.builder()
                .participantsNumber(participantsNumber)
                .status(status)
                .build();
    }
}
