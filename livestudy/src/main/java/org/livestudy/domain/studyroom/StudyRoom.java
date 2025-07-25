package org.livestudy.domain.studyroom;

import jakarta.persistence.*;
import lombok.*;
import org.livestudy.domain.BaseEntity;
import org.livestudy.exception.CustomException;
import org.livestudy.exception.ErrorCode;

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
    private Integer capacity = 20; // 논의 결과를 토대로 20명으로 정합니다.

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StudyRoomStatus status;

    public static StudyRoom of(Integer participantsNumber, int capacity,
                               StudyRoomStatus status) {

        return StudyRoom.builder()
                .capacity(capacity)
                .participantsNumber(participantsNumber)
                .status(status)
                .build();
    }

    public void incrementParticipantsNumber() {
        if(participantsNumber < capacity){
            this.participantsNumber++;
        } else {
            throw new CustomException(ErrorCode.ROOM_IS_FULL);
        }
    }

    public void updateStatus(StudyRoomStatus studyRoomStatus) {
        this.status = studyRoomStatus;
    }
}
