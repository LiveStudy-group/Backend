package org.livestudy.domain.user.statusdata;

import jakarta.persistence.*;
import lombok.*;
import org.livestudy.domain.user.User;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "daily_study_records")
public class DailyStudyRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    @Builder.Default
    @Column(name = "daily_study_time", nullable = false)
    private Integer dailyStudyTime = 0;

    @Builder.Default
    @Column(name = "daily_away_time", nullable = false)
    private Integer dailyAwayTime = 0;

    // 집중률
    @Transient
    public Double getFocusRatio() {
        if (dailyStudyTime + dailyAwayTime == 0) {
            return 0.0;
        }
        return (double) dailyStudyTime / (dailyStudyTime + dailyAwayTime) * 100;
    }
}
