package org.livestudy.dto;

import org.livestudy.domain.user.statusdata.UserActivity;
import org.livestudy.domain.user.statusdata.UserStudyStat;

public record GrantTitleRequest(String userId, UserActivity activity, UserStudyStat stat) {
}
