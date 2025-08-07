package org.livestudy.domain.title;

import jakarta.persistence.*;
import lombok.*;
import org.livestudy.domain.BaseEntity;
import org.livestudy.domain.user.User;

import java.time.LocalDateTime;

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

    private LocalDateTime acquiredAt; // 획득한 날짜 및 시간

    private boolean isEquipped; // 지금 착용중인지 여부

    public static UserTitle create(User user, Title title) {

        return UserTitle.builder()
                .user(user)
                .title(title)
                .acquiredAt(LocalDateTime.now())
                .isEquipped(false)
                .build();
    }

    public static UserTitle grant(User user, Title title) {
        return create(user, title);

    }


    public void equip() {
        this.isEquipped = true;
    }

    public void unequip() {
        this.isEquipped = false;
    }
}
