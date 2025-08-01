package org.livestudy.repository.titlecondition;

import lombok.RequiredArgsConstructor;
import org.livestudy.component.UserActivityFactory;
import org.livestudy.domain.title.TitleCode;
import org.livestudy.domain.title.TitleCondition;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class TitleCollectCondition implements TitleCondition {

    private final UserActivityFactory factory;

    @Override
    public boolean isSatisfied(Long userId) {
        return factory.getEarnedTitleCount(userId) >= 10;
    }

    @Override
    public TitleCode getTitleCode() {
        return TitleCode.TITLE_COLLECTOR;
    }
}
