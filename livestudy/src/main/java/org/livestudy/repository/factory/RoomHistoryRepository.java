package org.livestudy.repository.factory;

import org.livestudy.domain.data.RoomHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomHistoryRepository extends JpaRepository<RoomHistory, Long> {


    boolean existsByUserId(Long userId);

}
