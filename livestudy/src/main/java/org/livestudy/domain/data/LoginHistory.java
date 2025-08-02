package org.livestudy.domain.data;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

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

    private Long userId;

    private LocalDate loginDate;

    private LocalTime loginTime;

    public static LoginHistory of(Long userId, LocalDate loginDate, LocalTime loginTime) {
        return LoginHistory.builder()
                .userId(userId)
                .loginDate(loginDate)
                .loginTime(loginTime)
                .build();
    }
}
