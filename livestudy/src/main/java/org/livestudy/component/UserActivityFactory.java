package org.livestudy.component;

import lombok.RequiredArgsConstructor;
import org.livestudy.repository.UserRepository;
import org.livestudy.repository.UserStudyStatRepository;
import org.livestudy.repository.UserTitleRepository;
import org.livestudy.repository.DailyStudyRecordRepository;
import org.livestudy.repository.factory.RoomHistoryRepository;
import org.livestudy.repository.factory.UserChatRepository;
import org.livestudy.repository.factory.UserLoginHistoryRepository;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserActivityFactory {

    private final UserRepository userRepository;
    private final DailyStudyRecordRepository dailyStudyRecordRepository;
    private final UserStudyStatRepository userStudyStatRepository;
    private final UserLoginHistoryRepository loginHistoryRepository;
    private final UserChatRepository chatRepository;
    private final UserReportRepository reportRepository;
    private final UserTitleRepository titleRepository;
    private final RoomHistoryRepository roomHistoryRepository;
}
