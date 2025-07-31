package org.livestudy.repository.factory;

import org.livestudy.domain.studyroom.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserChatRepository extends JpaRepository<Chat, Long> {

    long countByUserId(Long userId);
}
