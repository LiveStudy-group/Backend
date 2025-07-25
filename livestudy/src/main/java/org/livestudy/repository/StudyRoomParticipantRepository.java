package org.livestudy.repository;

import org.livestudy.domain.studyroom.StudyRoom;
import org.livestudy.domain.studyroom.StudyRoomParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudyRoomParticipantRepository extends JpaRepository<StudyRoomParticipant, Long> {

    Optional<StudyRoomParticipant> findByStudyRoomAndUserId(StudyRoom studyRoom, Long userId);

}
