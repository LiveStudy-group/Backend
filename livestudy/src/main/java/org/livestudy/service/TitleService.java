package org.livestudy.service;


import org.livestudy.domain.title.Title;
import org.livestudy.domain.user.User;
import org.livestudy.domain.user.statusdata.UserActivity;
import org.livestudy.domain.user.statusdata.UserStudyStat;

import java.util.List;

public interface TitleService {

    List<Title> evaluateAndGrantTitles(Long userId);

    void equipTitle(Long userId, Long titleId);
}
