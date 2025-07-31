package org.livestudy.dto;

import java.util.List;

public record GrantTitleResponse(
        List<String> grantedTitleNames // 획득한 칭호의 이름
) {
}
