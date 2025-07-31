package org.livestudy.domain.data;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class LoginHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private LocalDateTime loginTime;

    public static LoginHistory of(Long userId, LocalDateTime loginTime) {
        return LoginHistory.builder()
                .userId(userId)
                .loginTime(loginTime)
                .build();
    }
}
