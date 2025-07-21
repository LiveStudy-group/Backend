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
    private Integer participantsNumber = 0; // 이 친구는 Redis Database로 관리할 예정이라... 아마 없어져야 할 것 같아요.

    @Column(name = "capacity", nullable = false)
    @Builder.Default
    private Integer capacity = 500; // 최대 정원은 따로 정하지 않은 것 같아서, 500명으로 일단 설정해 놓았습니다.

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
