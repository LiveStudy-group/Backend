package org.livestudy.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.livestudy.domain.user.User;
import org.livestudy.domain.user.statusdata.UserStudyStat;
import org.livestudy.dto.UserProfile.*;
import org.livestudy.exception.CustomException;
import org.livestudy.exception.ErrorCode;
import org.livestudy.repository.UserRepository;
import org.livestudy.repository.UserStudyStatRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @InjectMocks
    private ProfileServiceImpl profileService;

    @Mock
    private UserRepository userRepo;
    @Mock
    private UserStudyStatRepository userStudyStatRepo;
    @Mock
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private UserStudyStat testUserStudyStat;

    @BeforeEach
    void setUp() {
        // 테스트용 User 객체 생성
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encodedPassword123")
                .nickname("testuser")
                .profileImage("http://example.com/image.jpg")
                .build();

        // 테스트용 UserStudyStat 객체 생성
        testUserStudyStat = UserStudyStat.builder()
                .id(1L)
                .user(testUser)
                .totalStudyTime(1200)
                .build();
    }

    @Test
    @DisplayName("프로필 조회 성공")
    void getUserProfile_Success() {
        // Given (상황 설정)
        given(userRepo.findById(testUser.getId())).willReturn(Optional.of(testUser));
        given(userStudyStatRepo.findByUserId(testUser.getId())).willReturn(Optional.of(testUserStudyStat));

        // When (행위)
        UserProfileResponse response = profileService.getUserProfile(testUser.getId());

        // Then (결과 검증)
        assertThat(response).isNotNull();
        assertThat(response.getNickname()).isEqualTo(testUser.getNickname());
        assertThat(response.getEmail()).isEqualTo(testUser.getEmail());
        assertThat(response.getProfileImage()).isEqualTo(testUser.getProfileImage());
        assertThat(response.getTotalStudyTime()).isEqualTo(testUserStudyStat.getTotalStudyTime());

        // verify (호출 검증)
        verify(userRepo).findById(testUser.getId());
        verify(userStudyStatRepo).findByUserId(testUser.getId());
    }

    @Test
    @DisplayName("프로필 조회 실패 - 유저를 찾을 수 없음")
    void getUserProfile_UserNotFound() {
        // Given
        given(userRepo.findById(any(Long.class))).willReturn(Optional.empty());

        // When & Then
        CustomException exception = assertThrows(CustomException.class,
                () -> profileService.getUserProfile(999L)); // 존재하지 않는 ID

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
        verify(userRepo).findById(999L);
        verify(userStudyStatRepo, never()).findByUserId(any(Long.class)); // 유저 없으면 통계 조회 안 함
    }

    @Test
    @DisplayName("닉네임 변경 성공")
    void updateNickname_Success() {
        // Given
        String newNickname = "newuser";
        UpdateNicknameRequest request = new UpdateNicknameRequest(newNickname);

        given(userRepo.findById(testUser.getId())).willReturn(Optional.of(testUser));
        given(userRepo.existsByNickname(newNickname)).willReturn(false); // 중복 아님

        // When
        profileService.updateNickname(testUser.getId(), request);

        // Then (확인)
        assertThat(testUser.getNickname()).isEqualTo(newNickname);
        verify(userRepo).save(testUser);
    }

    @Test
    @DisplayName("닉네임 변경 실패 - 현재 닉네임과 동일")
    void updateNickname_SameNickname() {
        // Given
        String currentNickname = testUser.getNickname();
        UpdateNicknameRequest request = new UpdateNicknameRequest(currentNickname);

        given(userRepo.findById(testUser.getId())).willReturn(Optional.of(testUser));

        // When & Then
        CustomException exception = assertThrows(CustomException.class,
                () -> profileService.updateNickname(testUser.getId(), request));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SAME_NICKNAME);
        verify(userRepo, never()).existsByNickname(any(String.class));
        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    @DisplayName("닉네임 변경 실패 - 이미 사용 중인 닉네임")
    void updateNickname_DuplicateNickname() {
        // Given
        String newNickname = "existinguser";
        UpdateNicknameRequest request = new UpdateNicknameRequest(newNickname);

        given(userRepo.findById(testUser.getId())).willReturn(Optional.of(testUser));
        given(userRepo.existsByNickname(newNickname)).willReturn(true); // 중복임

        // When & Then
        CustomException exception = assertThrows(CustomException.class,
                () -> profileService.updateNickname(testUser.getId(), request));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.DUPLICATE_NICKNAME);
        verify(userRepo).existsByNickname(newNickname);
        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    @DisplayName("닉네임 변경 실패 - 유저를 찾을 수 없음")
    void updateNickname_UserNotFound() {
        // Given
        UpdateNicknameRequest request = new UpdateNicknameRequest("anyNickname");
        given(userRepo.findById(any(Long.class))).willReturn(Optional.empty());

        // When & Then
        CustomException exception = assertThrows(CustomException.class,
                () -> profileService.updateNickname(999L, request));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
        verify(userRepo, never()).existsByNickname(any(String.class));
        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    @DisplayName("프로필 이미지 변경 성공")
    void updateProfileImage_Success() {
        // Given
        String newImageUrl = "http://new.example.com/image.jpg";
        UpdateProfileImageRequest request = new UpdateProfileImageRequest(newImageUrl);

        given(userRepo.findById(testUser.getId())).willReturn(Optional.of(testUser));

        // When
        profileService.updateProfileImage(testUser.getId(), request);

        // Then
        assertThat(testUser.getProfileImage()).isEqualTo(newImageUrl);
        verify(userRepo).save(testUser);
    }

    @Test
    @DisplayName("프로필 이미지 변경 성공 - 현재 이미지와 동일하면 경고 후 저장 안함")
    void updateProfileImage_SameImage() {
        // Given
        String currentImageUrl = testUser.getProfileImage();
        UpdateProfileImageRequest request = new UpdateProfileImageRequest(currentImageUrl);

        given(userRepo.findById(testUser.getId())).willReturn(Optional.of(testUser));

        // When
        profileService.updateProfileImage(testUser.getId(), request);

        // Then (save 메서드가 호출되지 않았는지 확인)
        verify(userRepo, never()).save(any(User.class));
        // User 엔티티의 프로필 이미지는 변경되지 않아야 함
        assertThat(testUser.getProfileImage()).isEqualTo(currentImageUrl);
    }

    @Test
    @DisplayName("프로필 이미지 변경 실패 - 유저를 찾을 수 없음")
    void updateProfileImage_UserNotFound() {
        // Given
        UpdateProfileImageRequest request = new UpdateProfileImageRequest("anyImageUrl");
        given(userRepo.findById(any(Long.class))).willReturn(Optional.empty());

        // When & Then
        CustomException exception = assertThrows(CustomException.class,
                () -> profileService.updateProfileImage(999L, request));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    @DisplayName("이메일 변경 성공")
    void updateEmail_Success() {
        // Given
        String newEmail = "new@example.com";
        UpdateEmailRequest request = new UpdateEmailRequest(newEmail);

        given(userRepo.findById(testUser.getId())).willReturn(Optional.of(testUser));
        given(userRepo.existsByEmail(newEmail)).willReturn(false);

        // When
        profileService.updateEmail(testUser.getId(), request);

        // Then
        assertThat(testUser.getEmail()).isEqualTo(newEmail);
        verify(userRepo).save(testUser);
    }

    @Test
    @DisplayName("이메일 변경 실패 - 현재 이메일과 동일")
    void updateEmail_SameEmail() {
        // Given
        String currentEmail = testUser.getEmail();
        UpdateEmailRequest request = new UpdateEmailRequest(currentEmail);

        given(userRepo.findById(testUser.getId())).willReturn(Optional.of(testUser));

        // When & Then
        CustomException exception = assertThrows(CustomException.class,
                () -> profileService.updateEmail(testUser.getId(), request));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SAME_EMAIL);
        verify(userRepo, never()).existsByEmail(any(String.class));
        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    @DisplayName("이메일 변경 실패 - 이미 사용 중인 이메일")
    void updateEmail_DuplicateEmail() {
        // Given
        String newEmail = "another@example.com";
        UpdateEmailRequest request = new UpdateEmailRequest(newEmail);

        given(userRepo.findById(testUser.getId())).willReturn(Optional.of(testUser));
        given(userRepo.existsByEmail(newEmail)).willReturn(true); // 중복임

        // When & Then
        CustomException exception = assertThrows(CustomException.class,
                () -> profileService.updateEmail(testUser.getId(), request));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.DUPLICATE_EMAIL);
        verify(userRepo).existsByEmail(newEmail);
        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    @DisplayName("이메일 변경 실패 - 유저를 찾을 수 없음")
    void updateEmail_UserNotFound() {
        // Given
        UpdateEmailRequest request = new UpdateEmailRequest("any@email.com");
        given(userRepo.findById(any(Long.class))).willReturn(Optional.empty());

        // When & Then
        CustomException exception = assertThrows(CustomException.class,
                () -> profileService.updateEmail(999L, request));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
        verify(userRepo, never()).existsByEmail(any(String.class));
        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    @DisplayName("비밀번호 변경 성공")
    void updatePassword_Success() {
        // Given
        String currentPassword = "oldPassword123";
        String newPassword = "newPassword456";
        UpdatePasswordRequest request = new UpdatePasswordRequest(currentPassword, newPassword, newPassword); // confirmNewPassword는 백엔드에서 검증 안 함

        given(userRepo.findById(testUser.getId())).willReturn(Optional.of(testUser));
        given(passwordEncoder.matches(currentPassword, testUser.getPassword())).willReturn(true); // 현재 비밀번호 일치
        given(passwordEncoder.matches(newPassword, testUser.getPassword())).willReturn(false); // 새 비밀번호가 현재와 다름
        given(passwordEncoder.encode(newPassword)).willReturn("encodedNewPassword456");

        // When
        profileService.updatePassword(testUser.getId(), request);

        // Then
        assertThat(testUser.getPassword()).isEqualTo("encodedNewPassword456");
        verify(userRepo).save(testUser);
    }

    @Test
    @DisplayName("비밀번호 변경 실패 - 현재 비밀번호 불일치")
    void updatePassword_InvalidCurrentPassword() {
        // Given
        String wrongPassword = "wrongPassword";
        String newPassword = "newPassword456";
        UpdatePasswordRequest request = new UpdatePasswordRequest(wrongPassword, newPassword, newPassword);

        given(userRepo.findById(testUser.getId())).willReturn(Optional.of(testUser));
        given(passwordEncoder.matches(wrongPassword, testUser.getPassword())).willReturn(false); // 현재 비밀번호 불일치

        // When & Then
        CustomException exception = assertThrows(CustomException.class,
                () -> profileService.updatePassword(testUser.getId(), request));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_PASSWORD);
        verify(passwordEncoder).matches(wrongPassword, testUser.getPassword());
        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    @DisplayName("비밀번호 변경 실패 - 새 비밀번호가 현재 비밀번호와 동일")
    void updatePassword_SameAsCurrentPassword() {
        // Given
        String currentPassword = "oldPassword123";
        String newPassword = "oldPassword123";
        UpdatePasswordRequest request = new UpdatePasswordRequest(currentPassword, newPassword, newPassword);

        given(userRepo.findById(testUser.getId())).willReturn(Optional.of(testUser));
        given(passwordEncoder.matches(currentPassword, testUser.getPassword())).willReturn(true); // 현재 비밀번호 일치
        given(passwordEncoder.matches(newPassword, testUser.getPassword())).willReturn(true); // 새 비밀번호가 현재와 동일

        // When & Then
        CustomException exception = assertThrows(CustomException.class,
                () -> profileService.updatePassword(testUser.getId(), request));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SAME_PASSWORD);
        verify(passwordEncoder).matches(currentPassword, testUser.getPassword());
        verify(passwordEncoder).matches(newPassword, testUser.getPassword());
        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    @DisplayName("비밀번호 변경 실패 - 유저를 찾을 수 없음")
    void updatePassword_UserNotFound() {
        // Given
        UpdatePasswordRequest request = new UpdatePasswordRequest("current", "new", "new");
        given(userRepo.findById(any(Long.class))).willReturn(Optional.empty());

        // When & Then
        CustomException exception = assertThrows(CustomException.class,
                () -> profileService.updatePassword(999L, request));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
        verify(userRepo, never()).save(any(User.class));
    }
}