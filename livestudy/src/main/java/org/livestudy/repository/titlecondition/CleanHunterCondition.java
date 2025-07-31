package org.livestudy.repository.titlecondition;

import org.livestudy.domain.title.TitleCode;
import org.livestudy.domain.title.TitleCondition;
import org.livestudy.domain.user.statusdata.UserActivity;
import org.livestudy.domain.user.statusdata.UserStudyStat;

public class CleanHunterCondition implements TitleCondition {
    @Override
    public boolean isSatisfied(UserActivity activity, UserStudyStat stat) {
        return activity.getDoReportCount() >= 5;
    }

    @Override
    public TitleCode getTitleCode() {
        return TitleCode.CLEAN_HUNTER;
    }
}
