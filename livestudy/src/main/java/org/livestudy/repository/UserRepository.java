package org.livestudy.repository;

import jakarta.transaction.Transactional;
import org.livestudy.domain.user.SocialProvider;
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

    // 닉네임으로 사용자 검색 (소셜 로그인 시 중복 닉네임 체크용)
    Optional<User> findByNickname(String nickname);

    // 이메일과 소셜 프로바이더로 사용자 검색
    Optional<User> findByEmailAndSocialProvider(String email, SocialProvider socialProvider);

    Optional<User> findByUserId(Long userId);
}
