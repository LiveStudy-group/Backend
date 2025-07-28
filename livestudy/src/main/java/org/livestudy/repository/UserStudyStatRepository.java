package org.livestudy.repository;

import org.livestudy.domain.user.UserStudyStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserStudyStatRepository extends JpaRepository<UserStudyStat, Long> {

    Optional<UserStudyStat> findByUserId(Long userId);
}
