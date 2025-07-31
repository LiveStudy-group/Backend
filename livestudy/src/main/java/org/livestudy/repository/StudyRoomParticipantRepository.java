package org.livestudy.repository;

import org.livestudy.domain.studyroom.StudyRoom;
import org.livestudy.domain.studyroom.StudyRoomParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.livestudy.domain.studyroom.FocusStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import java.util.Optional;

public interface StudyRoomParticipantRepository extends JpaRepository<StudyRoomParticipant, Long> {
  
    // 특정 사용자의 활성 참여 정보 조회
    Optional<StudyRoomParticipant> findByUserIdAndStudyRoomIdAndLeaveTimeIsNull(Long userId, Long roomId);

    // 특정 방의 모든 참여자 조회
    List<StudyRoomParticipant> findByStudyRoomIdAndLeaveTimeIsNull(Long roomId);

    // 특정 상태이면서 일정 시간 이상 지난 사용자들 조회
    @Query("SELECT p FROM StudyRoomParticipant p WHERE p.focusStatus = :status AND p.statusChangedAt < :threshold AND p.leaveTime IS NULL")
    List<StudyRoomParticipant> findByStatusAndStatusChangedAtBeforeAndLeaveTimeIsNull(
            @Param("status") FocusStatus status,
            @Param("threshold") LocalDateTime threshold
    );

    Optional<StudyRoomParticipant> findByStudyRoomAndUserId(StudyRoom studyRoom, Long userId);
}
