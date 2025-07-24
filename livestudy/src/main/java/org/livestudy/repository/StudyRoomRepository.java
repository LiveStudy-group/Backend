package org.livestudy.repository;

import org.livestudy.domain.studyroom.StudyRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyRoomRepository extends JpaRepository<StudyRoom, Long> {
}
