package org.livestudy.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.livestudy.domain.user.User;
import org.livestudy.domain.user.UserStudyStat;
import org.livestudy.dto.UserProfile.*;
import org.livestudy.exception.CustomException;
import org.livestudy.exception.ErrorCode;
import org.livestudy.repository.UserRepository;
import org.livestudy.repository.UserStudyStatRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService{

    private final UserRepository userRepo;
    private final UserStudyStatRepository userStudyStatRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserProfileResponse getUserProfile(Long userId) {

        // User 엔티티 조회
        User user = getUserById(userId);

        // UserStudyStat 엔티티 조회
        UserStudyStat userStudyStat = userStudyStatRepo.findByUserId(userId)
                .orElse(null);

        // 칭호
        String selectedTitleName = null;
        if (user.getUserTitle() != null && user.getUserTitle().getTitle() != null) {
            selectedTitleName = user.getUserTitle().getTitle().getName();
        }

        return UserProfileResponse.builder()
                .profileImage(user.getProfileImage())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .selectedTitle(selectedTitleName)
                .totalStudyTime(userStudyStat != null ?
                        userStudyStat.getTotalStudyTime() : 0)
                .totalAttendanceDays(userStudyStat != null ?
                        userStudyStat.getTotalAttendanceDays() : 0)
                .continueAttendanceDays(userStudyStat != null ?
                        userStudyStat.getContinueAttendanceDays() : 0)
                .build();
    }

    @Override
    @Transactional
    public void updateNickname(Long userId, UpdateNicknameRequest request) {

        // User 엔티티 조회
        User user = getUserById(userId);
        String newNickname = request.getNewNickname();

        // 현재 닉네임과 동일한지 체크
        if (user.getNickname().equals(newNickname)) {
            log.error("userId: {} 유저의 닉네임 변경이 실패하였습니다. 사유: SAME_NICKNAME", userId);
            throw new CustomException(ErrorCode.SAME_NICKNAME);
        }
        // 사용 중인 닉네임인지 체크
        if (userRepo.existsByNickname(newNickname)) {
            log.error("userId: {} 유저의 닉네임 변경이 실패하였습니다. 사유: DUPLICATE_NICKNAME", userId);
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }


        user.updateNickname(newNickname);
        userRepo.save(user);
        log.info("userId: {} 유저의 닉네임이 '{}'로 변경되었습니다.", userId, newNickname);


    }

    @Override
    @Transactional
    public void updateProfileImage(Long userId, UpdateProfileImageRequest request) {

        // User 엔티티 조회
        User user = getUserById(userId);
        String newProfileImage = request.getNewProfileImage();

        // 현재 이미지와 동일한지 체크
        if (user.getProfileImage() != null && user.getProfileImage().equals(newProfileImage)) {
            log.warn("userId: {} 유저의 프로필 이미지가 동일한 이미지로 변경이 요청되었습니다.", userId);
            return;
        }

        user.updateProfileImage(newProfileImage);
        userRepo.save(user);
        log.info("userId: {} 유저의 프로필 이미지가 변경되었습니다.", userId);

    }

    @Override
    @Transactional
    public void updateEmail(Long userId, UpdateEmailRequest request) {

        // User 엔티티 조회
        User user = getUserById(userId);
        String newEmail = request.getNewEmail();

        // 현재 이메일과 동일한지 체크
        if (user.getEmail().equals(newEmail)) {
            log.error("userId: {} 유저의 이메일 변경이 실패하였습니다. 사유: SAME_EMAIL", userId);
            throw new CustomException(ErrorCode.SAME_EMAIL);
        }
        // 이미 사용 중인 이메일인지 체크
        if (userRepo.existsByEmail(newEmail)) {
            log.error("userId: {} 유저의 이메일 변경이 실패하였습니다. 사유: DUPLICATE_EMAIL", userId);
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }


        user.updateEmail(newEmail);
        userRepo.save(user);
        log.info("userId: {} 유저의 이메일이 '{}'로 변경되었습니다.", userId, newEmail);

    }

    @Override
    @Transactional
    public void updatePassword(Long userId, UpdatePasswordRequest request) {

        // User 엔티티 조회
        User user = getUserById(userId);

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            log.error("userId: {} 유저의 비밀번호가 일치하지 않아 변경에 실패합니다.", userId);
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }
        String newPassword = request.getNewPassword();

        // 현재 비밀번호와 동일한지 체크
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            log.error("userId: {} 유저의 비밀번호 변경이 실패하였습니다. 사유: SAME_PASSWORD", userId);
            throw new CustomException(ErrorCode.SAME_PASSWORD);
        }

        user.updatePassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
        log.info("userId: {} 유저의 비밀번호가 변경되었습니다.", userId);

    }

    private User getUserById(Long userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> {
                    log.error("userId: {} 유저를 찾을 수 없습니다.", userId);
                    return new CustomException(ErrorCode.USER_NOT_FOUND);
                });
    }


}
