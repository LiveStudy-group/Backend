package org.livestudy.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepo;
    private final StudyRoomRepository studyRoomRepo;
    private final StudyRoomParticipantRepository studyRoomParticipantRepo;

    @Transactional
    public void saveChat(String roomId, String senderId, String message) {
        Long longRoomId = Long.parseLong(roomId);
        StudyRoom studyRoom = studyRoomRepo.findById(longRoomId)
                .orElseGet(() -> {
                    log.error(" {} 번의 스터디룸을 찾을 수 없습니다.", longRoomId);
                    throw new CustomException(ErrorCode.ROOM_NOT_FOUND);
                });
        Long longSenderId = Long.parseLong(senderId);
        StudyRoomParticipant participant = studyRoomParticipantRepo
                .findByStudyRoomAndUserId(studyRoom, longSenderId)
                .orElseGet(() -> {
                    log.error(" {} 유저가 해당 {} 스터디룸에 존재하지 않습니다.", longSenderId, longRoomId);
                    throw new CustomException(ErrorCode.USER_NOT_IN_ROOM);
                });

        Chat chat = Chat.builder()
                .studyRoom(studyRoom)
                .participant(participant)
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();

        chatRepo.save(chat);
    }
}
