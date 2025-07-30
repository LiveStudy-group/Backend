package org.livestudy.repository;

import org.livestudy.domain.title.Title;
import org.livestudy.domain.title.UserTitle;
import org.livestudy.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserTitleRepository extends JpaRepository<UserTitle, Long> {

    boolean existsByUserAndTitle(User user, Title title);

    Optional<UserTitle> findByUserAndTitle(User user, Title title);

    List<UserTitle> findAllByUserAndIsEquippedTrue(User user);
}
