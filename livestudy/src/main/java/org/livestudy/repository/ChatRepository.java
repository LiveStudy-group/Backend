package org.livestudy.repository;

import org.livestudy.domain.studyroom.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    // 사용자 기준 채팅 수 세기
    long countByParticipant_User_Id(Long userId);

    // 방 기준 채팅 찾기
    List<Chat> findByStudyRoom_Id(Long roomId);

}