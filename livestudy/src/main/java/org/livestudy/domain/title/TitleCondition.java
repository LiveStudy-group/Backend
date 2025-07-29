package org.livestudy.domain.title;

import org.livestudy.domain.user.UserActivity;

public interface TitleCondition {

    boolean isSatisfied(UserActivity activity);
    TitleCode getTitleCode();
}
