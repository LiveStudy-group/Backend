package org.livestudy.repository.factory;

import io.lettuce.core.dynamic.annotation.Param;
import org.livestudy.domain.data.LoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Repository
public interface UserLoginHistoryRepository extends JpaRepository<LoginHistory, Long> {


    @Query("""
        SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END
        FROM LoginHistory l
        WHERE l.user.id = :userId
          AND FUNCTION('DATE', l.loginTime) = :date
          AND FUNCTION('HOUR', l.loginTime) = :hour
    """)
    boolean existsLoginAtHour(@Param("userId")Long userId,
                              @Param("date") LocalDate date,
                              @Param("hour")int hour);


    @Query("""
    SELECT l FROM LoginHistory l
    WHERE l.user.id = :userId
    ORDER BY l.loginTime DESC
    LIMIT 1
""")
    Optional<LocalTime> findLastLoginTime(Long userId);
}
