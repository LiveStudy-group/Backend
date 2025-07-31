package org.livestudy.repository.titlecondition;

import org.livestudy.domain.title.TitleCode;
import org.livestudy.domain.title.TitleCondition;
import org.livestudy.domain.user.statusdata.UserActivity;

public class FocusBeginnerCondition implements TitleCondition {

    @Override
    public TitleCode getTitleCode() {
        return  TitleCode.FOCUS_BEGINNER;
    }

    @Override
    public boolean isSatisfied(UserActivity activity) {
        return activity.getOneDayFocusMinutes() >= 30;
    }
}
