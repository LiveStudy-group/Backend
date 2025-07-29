package org.livestudy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AverageFocusRatioResponse {
    private LocalDate startDate;
    private LocalDate endDate;
    private Double averageFocusRatio;
}
