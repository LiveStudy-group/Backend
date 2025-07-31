package org.livestudy.service;


import org.livestudy.domain.title.Title;
import org.livestudy.domain.user.User;
import org.livestudy.domain.user.UserActivity;

import java.util.List;

public interface TitleService {

    List<Title> evaluateAndGrantTitles(User user, UserActivity activity);

    void equipTitle(Long userId, Long titleId);
}
