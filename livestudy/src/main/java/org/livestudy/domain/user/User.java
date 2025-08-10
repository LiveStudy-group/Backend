package org.livestudy.domain.user;

import jakarta.persistence.*;
import lombok.*;
import org.livestudy.domain.BaseEntity;
import org.livestudy.domain.badge.Badge;
import org.livestudy.domain.title.UserTitle;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "users", uniqueConstraints = @UniqueConstraint(name = "uk_user_email", columnNames = "email"))
@EntityListeners(AuditingEntityListener.class)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_title_id")
    private UserTitle userTitle;

    // 계정 정보
    @Column(nullable = false, length = 100)
    private String email;

    @Column(length = 70) // nullable로 변경 (소셜 로그인 시 비밀번호 없음)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "social_provider", nullable = false)
    private SocialProvider socialProvider;

    //프로필
    @Column(nullable = false, length = 20)
    private String nickname;

    @Column
    private String introduction;

    @Lob
    @Column(name = "profile_image")
    private String profileImage;

    //신고
    @Builder.Default
    @Column(name = "total_report", nullable = false)
    private Integer totalReport = 0;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "user_status", nullable = false)
    private UserStatus userStatus = UserStatus.NORMAL;

    @Transient
    private boolean isNewUser;

    // 이메일 회원가입용
    public static User of(UserTitle userTitle,
                          String email,
                          String password,
                          SocialProvider provider,
                          String nickname,
                          String introduction,
                          String image, PasswordEncoder encoder) {

        return User.builder()
                .userTitle(userTitle)
                .email(email)
                .password(password == null ? null : encoder.encode(password))
                .socialProvider(provider)
                .nickname(nickname)
                .introduction(introduction)
                .profileImage(image)
                .build();

    }
    // 소셜 로그인용
    public static User ofSocial(String email,
                                String nickname,
                                String profileImage,
                                SocialProvider socialProvider,
                                String socialId) {

        //이메일이 없을 경우 임의의 이메일 생성
        if (email == null || email.isBlank()) {
            email = socialProvider.name().toLowerCase() + "_" + socialId +"@livestudy.com";
        }

        return User.builder()
                .email(email)
                .nickname(nickname)
                .profileImage(profileImage)
                .socialProvider(socialProvider)
                .userStatus(UserStatus.NORMAL)
                .password(null) // 소셜 로그인은 비밀번호 없음
                .build();
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "equipped_badge")
    private Badge equippedBadge;

    public void equipBadge(Badge badge) {
        this.equippedBadge = badge;
    }


    // 닉네임 변경
    public void updateNickname(String newNickname) {
        this.nickname = newNickname;
    }

    // 프로필 이미지 변경
    public void updateProfileImage(String newProfileImage) {
        this.profileImage = newProfileImage;
    }

    // 이메일 변경
    public void updateEmail(String newEmail) {
        this.email = newEmail;
    }

    // 패스워드 변경
    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }
}