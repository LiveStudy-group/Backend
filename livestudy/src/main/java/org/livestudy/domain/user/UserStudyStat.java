package org.livestudy.domain.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserStudyStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long    id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    //총 출석 일수
    @Builder.Default
    @Column(name = "total_attendance_days", nullable = false)
    private Integer totalAttendanceDays = 0;

    //연속 출석 일수
    @Builder.Default
    @Column(name = "continue_attendance_days", nullable = false)
    private Integer continueAttendanceDays = 0;

    //마지막 출석 일수
    @Column(name = "last_attendance_date")
    private LocalDate lastAttendanceDate;

    //누적 공부 시간
    @Builder.Default
    @Column(name = "total_study_time", nullable = false)
    private Integer totalStudyTime = 0;

    //누적 자리비움 시간
    @Builder.Default
    @Column(name = "total_away_time", nullable = false)
    private Integer totalAwayTime = 0;
}
