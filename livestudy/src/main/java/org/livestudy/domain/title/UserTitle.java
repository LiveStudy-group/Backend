package org.livestudy.domain.title;

import jakarta.persistence.*;
import lombok.*;
import org.livestudy.domain.BaseEntity;
import org.livestudy.domain.user.User;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserTitle extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "title_id", nullable = false)
    private Title title;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public static UserTitle of(User user, Title title) {

        return UserTitle.builder()
                .user(user)
                .title(title)
                .build();
    }
}
