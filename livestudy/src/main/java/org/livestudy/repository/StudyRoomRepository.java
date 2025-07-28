package org.livestudy.repository;


import org.livestudy.domain.studyroom.StudyRoom;
import org.livestudy.domain.studyroom.StudyRoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudyRoomRepository extends JpaRepository<StudyRoom, Long> {

    List<StudyRoom> findByStatus(StudyRoomStatus status); // OPEN 상태의 Room을 확인하고 찾는 기능

    Optional<StudyRoom> findTopByStatusAndParticipantsNumberLessThanOrderByParticipantsNumberAsc(StudyRoomStatus status, Integer capacity);

}
