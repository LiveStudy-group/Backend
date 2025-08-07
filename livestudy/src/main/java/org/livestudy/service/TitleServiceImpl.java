package org.livestudy.service;

import jakarta.transaction.Transactional;
import org.livestudy.domain.title.Title;
import org.livestudy.domain.title.TitleCode;
import org.livestudy.domain.title.TitleCondition;
import org.livestudy.domain.title.UserTitle;
import org.livestudy.domain.user.User;
import org.livestudy.dto.UserTitleResponse;
import org.livestudy.exception.CustomException;
import org.livestudy.exception.ErrorCode;
import org.livestudy.repository.TitleRepository;
import org.livestudy.repository.UserRepository;
import org.livestudy.repository.UserTitleRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TitleServiceImpl implements TitleService {

    private final TitleRepository titleRepository;

    private final UserTitleRepository userTitleRepository;

    private final UserRepository userRepository;

    private final List<TitleCondition> titleConditions;



    public TitleServiceImpl(TitleRepository titleRepository, UserTitleRepository userTitleRepository, UserRepository userRepository, List<TitleCondition> titleConditions) {
        this.titleRepository = titleRepository;
        this.userTitleRepository = userTitleRepository;
        this.userRepository = userRepository;


        this.titleConditions = titleConditions;
    }

    @Transactional
    public List<Title> evaluateAndGrantTitles(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<Title> grantedTitles = new ArrayList<>();

        for (TitleCondition condition : titleConditions) {
            TitleCode code = condition.getTitleCode();
            Title title = titleRepository.findByCode(code)
                    .orElseThrow(() -> new CustomException(ErrorCode.TITLE_NOT_FOUND));

            boolean alreadyOwned = userTitleRepository.existsByUserAndTitle(user, title);
            if (!alreadyOwned && condition.isSatisfied(userId)) {
                userTitleRepository.save(UserTitle.create(user, title));
                grantedTitles.add(title);
            }
        }

        return grantedTitles;
    }

    @Override
    public UserTitleResponse equipTitle(Long userId, Long titleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Title title = titleRepository.findById(titleId)
                .orElseThrow(() -> new CustomException(ErrorCode.TITLE_NOT_FOUND));

        UserTitle userTitle = userTitleRepository.findByUserAndTitle(user, title)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EARNED_TITLE_YET));

        // 1. 기존 장착 칭호 해제 (장착 중인 것만 해제)
        userTitleRepository.findAllByUserAndIsEquippedTrue(user)
                .forEach(UserTitle::unequip);

        // 2. 선택한 칭호 장착
        userTitle.equip();

        // 3. 뱃지 자동 장착
        user.equipBadge(title.getBadge());

        // 4. 변경된 User 저장
        userRepository.save(user);
        return null;
    }

    @Override
    public List<UserTitleResponse> getUserTitles(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<UserTitle> userTitles = userTitleRepository.findAllByUser(user);

        return userTitles.stream()
                .map(UserTitleResponse::from)
                .toList();
    }

}
