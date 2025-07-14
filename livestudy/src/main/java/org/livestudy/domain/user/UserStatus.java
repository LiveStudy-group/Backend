package org.livestudy.domain.user;

public enum UserStatus {
    NORMAL, // 평상시
    TEMPORARY_BAN,  // 정지 당할 시 Status
    PERMANENT_BAN  // 영구 삭제 당할 시 Status
}
