package org.livestudy.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.livestudy.domain.studyroom.*;
import org.livestudy.domain.user.User;
import org.livestudy.exception.CustomException;
import org.livestudy.exception.ErrorCode;
import org.livestudy.repository.ChatRepository;
import org.livestudy.repository.StudyRoomParticipantRepository;
import org.livestudy.repository.StudyRoomRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceUnitTest {

    @Mock
    private ChatRepository chatRepo;
    @Mock
    private StudyRoomRepository studyRoomRepo;
    @Mock
    private StudyRoomParticipantRepository studyRoomParticipantRepo;

    @InjectMocks
    private ChatService chatService;

    private StudyRoom testStudyRoom;
    private StudyRoomParticipant testParticipant;
    private User testUser;

    private final Long testRoomId = 1L;
    private final Long testSenderId = 10L;
    private final String testMessageContent = "안녕하세요.";

    @BeforeEach // 각 테스트 메서드가 실행되기 전에 호출
    void setUp() {

        testUser = mock(User.class);

        testStudyRoom = StudyRoom.builder()
                .id(1L)
                .participantsNumber(5)
                .status(StudyRoomStatus.OPEN)
                .build();

        testParticipant = StudyRoomParticipant.builder()
                .id(10L)
                .user(testUser)
                .studyRoom(testStudyRoom)
                .joinTime(LocalDateTime.now())
                .focusStatus(FocusStatus.FOCUS)
                .build();
    }

    @Test
    @DisplayName("채팅 메시지 저장 성공")
    void saveChat_Success() {
        // given
        // studyRoomRepo.findById(1L) 호출 시 testStudyRoom 반환하도록 Mocking
        when(studyRoomRepo.findById(testRoomId))
                .thenReturn(Optional.of(testStudyRoom));
        // studyRoomParticipantRepo.findByStudyRoomAndUserId(testStudyRoom, testSenderId) 호출 시 testParticipant 반환하도록 Mocking
        when(studyRoomParticipantRepo.findByStudyRoomAndUserId(eq(testStudyRoom), eq(testSenderId)))
                .thenReturn(Optional.of(testParticipant));
        // chatRepo.save(any(Chat.class)) 호출 시 any(Chat.class) 객체 그대로 반환하도록 Mocking
        when(chatRepo.save(any(Chat.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        chatService.saveChat(testRoomId.toString(), testSenderId.toString(), testMessageContent);

        // then
        // save 메서드가 1번 호출되었는지 검증
        verify(chatRepo, times(1)).save(any(Chat.class));
        // studyRoomRepo.findById와 studyRoomParticipantRepo.findByStudyRoomAndUserId가 각각 1번씩 호출되었는지 검증
        verify(studyRoomRepo, times(1)).findById(testRoomId);
        verify(studyRoomParticipantRepo, times(1)).findByStudyRoomAndUserId(eq(testStudyRoom), eq(testSenderId));
    }

    @Test
    @DisplayName("존재하지 않는 방 ID로 채팅 메시지 저장 시 예외 발생")
    void saveChat_RoomNotFound() {
        // given
        // studyRoomRepo.findById 호출 시 빈 Optional 반환하도록 Mocking
        when(studyRoomRepo.findById(testRoomId))
                .thenReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(CustomException.class, () ->
                chatService.saveChat(testRoomId.toString(), testSenderId.toString(), testMessageContent)
        );
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ROOM_NOT_FOUND);

        // chatRepo.save는 호출되지 않았음을 검증
        verify(chatRepo, never()).save(any(Chat.class));
        verify(studyRoomParticipantRepo, never()).findByStudyRoomAndUserId(any(), any());
    }

    @Test
    @DisplayName("방에 없는 사용자 ID로 채팅 메시지 저장 시 예외 발생")
    void saveChat_UserNotInRoom() {
        // given
        // studyRoomRepo.findById 호출 시 testStudyRoom 반환하도록 Mocking
        when(studyRoomRepo.findById(testRoomId))
                .thenReturn(Optional.of(testStudyRoom));
        // studyRoomParticipantRepo.findByStudyRoomAndUserId 호출 시 빈 Optional 반환하도록 Mocking
        when(studyRoomParticipantRepo.findByStudyRoomAndUserId(eq(testStudyRoom), eq(testSenderId)))
                .thenReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(CustomException.class, () ->
                chatService.saveChat(testRoomId.toString(), testSenderId.toString(), testMessageContent)
        );
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_IN_ROOM);

        // chatRepo.save는 호출되지 않았음을 검증
        verify(chatRepo, never()).save(any(Chat.class));
        verify(studyRoomRepo, times(1)).findById(testRoomId); // StudyRoom은 찾았지만 Participant를 못 찾음
    }
}