package org.livestudy.service;

import lombok.RequiredArgsConstructor;
import org.livestudy.domain.studyroom.Chat;
import org.livestudy.domain.studyroom.StudyRoom;
import org.livestudy.domain.studyroom.StudyRoomParticipant;
import org.livestudy.exception.CustomException;
import org.livestudy.exception.ErrorCode;
import org.livestudy.repository.ChatRepository;
import org.livestudy.repository.StudyRoomParticipantRepository;
import org.livestudy.repository.StudyRoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepo;
    private final StudyRoomRepository studyRoomRepo;
    private final StudyRoomParticipantRepository studyRoomParticipantRepo;

    @Transactional
    public void saveChat(String roomId, String senderId, String message) {
        Long longRoomId = Long.parseLong(roomId);
        StudyRoom studyRoom = studyRoomRepo.findById(longRoomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));

        Long longSenderId = Long.parseLong(senderId);
        StudyRoomParticipant participant = studyRoomParticipantRepo
                .findByStudyRoomAndUserId(studyRoom, longSenderId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_IN_ROOM));

        Chat chat = Chat.builder()
                .studyRoom(studyRoom)
                .participant(participant)
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();

        chatRepo.save(chat);
    }
}
