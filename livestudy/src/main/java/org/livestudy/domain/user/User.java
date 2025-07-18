package org.livestudy.domain.user;

import jakarta.persistence.*;
import lombok.*;
import org.livestudy.domain.BaseEntity;
import org.livestudy.domain.title.UserTitle;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "users", uniqueConstraints = @UniqueConstraint(name = "uk_user_email", columnNames = "email"))
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

    @Column(nullable = false, length = 70)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "social_provider", nullable = false)
    private SocialProvider socialProvider;

    //프로필
    @Column(nullable = false, length = 20)
    private String nickname;

    @Column
    private String introduction;

    @Column(name = "profile_image", length = 1024)
    private String profileImage;

    //신고
    @Builder.Default
    @Column(name = "total_report", nullable = false)
    private Integer totalReport = 0;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "user_status", nullable = false)
    private UserStatus userStatus = UserStatus.NORMAL;

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



}
