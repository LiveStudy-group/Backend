package org.livestudy.repository;

import org.livestudy.domain.studyroom.Chat;
import org.livestudy.domain.studyroom.StudyRoomParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyRoomParticipantRepository extends JpaRepository<StudyRoomParticipant, Long> {
}
