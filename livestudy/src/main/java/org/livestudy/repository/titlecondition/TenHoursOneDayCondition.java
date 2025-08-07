package org.livestudy.repository.titlecondition;

import lombok.RequiredArgsConstructor;
import org.livestudy.component.UserActivityFactory;
import org.livestudy.domain.title.TitleCode;
import org.livestudy.domain.title.TitleCondition;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
public class TenHoursOneDayCondition implements TitleCondition {

    private final UserActivityFactory factory;

    @Override
    public boolean isSatisfied(Long userId) {
        return factory.getOneDayFocusMinutes(userId) >= 600;
    }

    @Override
    public TitleCode getTitleCode() {
        return TitleCode.TEN_HOURS_ONE_DAY;
    }
}
