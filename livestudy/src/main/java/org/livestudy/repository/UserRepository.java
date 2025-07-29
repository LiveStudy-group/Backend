package org.livestudy.repository;

import jakarta.transaction.Transactional;
import org.livestudy.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Modifying
    @Transactional
    @Query("delete from User u where u.email = :email")
    void deleteByEmail(String email);
}
