package org.livestudy.repository.titlecondition;

import org.livestudy.domain.title.TitleCode;
import org.livestudy.domain.title.TitleCondition;
import org.livestudy.domain.user.UserActivity;

public class FirstRoomEnterCondition implements TitleCondition {
    @Override
    public TitleCode getTitleCode() {
        return TitleCode.FIRST_ROOM_ENTER;
    }

    @Override
    public boolean isSatisfied(UserActivity activity) {
        return activity.isEnteredFirstRoom();
    }
}
