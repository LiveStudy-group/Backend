package org.livestudy.repository.titlecondition;

import lombok.RequiredArgsConstructor;
import org.livestudy.component.UserActivityFactory;
import org.livestudy.domain.title.TitleCode;
import org.livestudy.domain.title.TitleCondition;
import org.livestudy.domain.user.statusdata.UserActivity;
import org.livestudy.domain.user.statusdata.UserStudyStat;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class FirstRoomEnterCondition implements TitleCondition {

    private final UserActivityFactory factory;

    @Override
    public TitleCode getTitleCode() {
        return TitleCode.FIRST_ROOM_ENTER;
    }

    @Override
    public boolean isSatisfied(Long userId) {
        return factory.hasEnteredAnyRoom(userId);
    }
}
