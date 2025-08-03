package org.livestudy.domain.data;


import jakarta.persistence.*;
import lombok.*;
import org.livestudy.domain.user.User;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class LoginHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDate loginDate;

    private LocalTime loginTime;

    public static LoginHistory of(User user, LocalDate loginDate, LocalTime loginTime) {
        return LoginHistory.builder()
                .user(user)
                .loginDate(loginDate)
                .loginTime(loginTime)
                .build();
    }
}
