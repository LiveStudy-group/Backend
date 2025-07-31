package org.livestudy.repository.factory;

import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Repository
public interface UserLoginHistoryRepository{

    boolean existsLoginAtHour(Long userId, LocalDate date, int hour);

    Optional<LocalTime> findLastLoginTime(Long userId);
}
