package org.livestudy.repository.titlecondition;

import lombok.RequiredArgsConstructor;
import org.livestudy.domain.title.TitleCode;
import org.livestudy.domain.title.TitleCondition;
import org.livestudy.domain.user.statusdata.UserActivity;
import org.livestudy.domain.user.statusdata.UserStudyStat;

@RequiredArgsConstructor
public class FocusRunnerCondition implements TitleCondition {


    @Override
    public boolean isSatisfied(UserActivity activity, UserStudyStat stat) {
        return activity.getRunConsecutiveFocusHour() >= 3;
    }

    @Override
    public TitleCode getTitleCode() {
        return TitleCode.FOCUS_RUNNER;
    }
}
