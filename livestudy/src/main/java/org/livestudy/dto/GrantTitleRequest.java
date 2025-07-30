package org.livestudy.dto;

import org.livestudy.domain.user.UserActivity;

public record GrantTitleRequest(String userId, UserActivity activity) {
}
