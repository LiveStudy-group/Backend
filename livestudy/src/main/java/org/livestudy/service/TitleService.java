package org.livestudy.service;


import org.livestudy.domain.title.Title;
import org.livestudy.dto.UserTitleResponse;

import java.util.List;

public interface TitleService {

    List<Title> evaluateAndGrantTitles(Long userId);

    UserTitleResponse equipTitle(Long userId, Long titleId);
}
