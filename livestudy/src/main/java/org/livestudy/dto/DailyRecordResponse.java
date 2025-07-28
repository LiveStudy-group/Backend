package org.livestudy.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class DailyRecordResponse {
    private LocalDate recordDate;
    private int dailyStudyTime;
    private int dailyAwayTime;
    private double focusRatio;
}
