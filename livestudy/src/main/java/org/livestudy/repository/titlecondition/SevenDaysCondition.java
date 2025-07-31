package org.livestudy.repository.titlecondition;

import org.livestudy.domain.title.TitleCode;
import org.livestudy.domain.title.TitleCondition;
import org.livestudy.domain.user.statusdata.UserActivity;
import org.livestudy.domain.user.statusdata.UserStudyStat;

public class SevenDaysCondition implements TitleCondition {
    @Override
    public boolean isSatisfied(UserActivity activity, UserStudyStat stat) {
        return activity.getConsecutiveLoginDays() >= 7;
    }

    @Override
    public TitleCode getTitleCode() {
        return TitleCode.SEVEN_DAYS;
    }
}
