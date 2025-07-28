package org.livestudy.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.livestudy.domain.studyroom.StudyRoom;
import org.livestudy.domain.studyroom.StudyRoomStatus;
import org.livestudy.exception.CustomException;
import org.livestudy.exception.ErrorCode;
import org.livestudy.repository.redis.RoomRedisRepository;
import org.livestudy.repository.StudyRoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;

import java.util.Optional;


@AllArgsConstructor
@Service
public class StudyRoomServiceImpl implements StudyRoomService {

    private final StudyRoomRepository studyRoomRepository;

    private final RoomRedisRepository roomRedisRepository;

    private static final int ROOM_CAPACITY = 20;

    private static final Logger log = LoggerFactory.getLogger(StudyRoomServiceImpl.class);

    @Override
    @Transactional
    public Long enterRoom(String userId) {

        // 1. 이미 입장한 방이 있는지 Redis에서 확인
        String existingRoomId = roomRedisRepository.getUserRoom(userId);
        if (existingRoomId != null) {
            throw new CustomException(ErrorCode.USER_ALREADY_IN_ROOM);
        }

        // 2. 참가자가 가장 적은 방을 찾음
        Optional<StudyRoom> targetRoom = studyRoomRepository
                .findTopByStatusAndParticipantsNumberLessThanOrderByParticipantsNumberAsc(
                        StudyRoomStatus.OPEN, ROOM_CAPACITY
                );
        StudyRoom assignedRoom;

        if (targetRoom.isEmpty()) {
            // 3-1. 조건을 만족하는 방이 없다면 새로 생성
            assignedRoom = StudyRoom.of(0, ROOM_CAPACITY, StudyRoomStatus.OPEN);
            studyRoomRepository.save(assignedRoom);
        } else {
            // 3-2. 가장 적은 인원이 있는 방 배정
            assignedRoom = targetRoom.get();
        }

        // 4. 인원 증가
        assignedRoom.incrementParticipantsNumber();
        if(assignedRoom.getParticipantsNumber().equals(assignedRoom.getCapacity())) {
            assignedRoom.updateStatus(StudyRoomStatus.FULL);
        }

        // 5. Redis에 유저-방 정보 저장
        roomRedisRepository.setUserRoom(userId, assignedRoom.getId().toString());

        return assignedRoom.getId();
    }



    @Override
    public void leaveRoom(String userId) {
        String roomId = roomRedisRepository.getUserRoom(userId);

        if(roomId != null) {
            roomRedisRepository.decrementRoomCount(roomId);
            roomRedisRepository.deleteUserRoom(userId);
        } else {
            throw new CustomException(ErrorCode.USER_NOT_IN_ROOM);
        }
    }

    @Override
    public String createRoom(int capacity) {

        // 정원이 맞지 않는 방이 생길 경우 방어 코드
        if (capacity != ROOM_CAPACITY) {
            throw new CustomException(ErrorCode.INVALID_ROOM_CAPACITY);
        }

        StudyRoom room = StudyRoom.of(0, capacity, StudyRoomStatus.OPEN);
        StudyRoom saved = studyRoomRepository.save(room);

        try {
            roomRedisRepository.incrementRoomCount(saved.getId().toString());
        } catch (DataAccessResourceFailureException ex) {
            // 예외 감지 시 롤백 또는 대체 처리
            log.error("Redis connection failed: {}", ex.getMessage());
            throw new CustomException(ErrorCode.REDIS_CONNECTION_FAILED);
        }

        return saved.getId().toString();
    }
}
