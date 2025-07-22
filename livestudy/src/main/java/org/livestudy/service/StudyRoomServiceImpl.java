package org.livestudy.service;

import lombok.AllArgsConstructor;
import org.livestudy.domain.studyroom.StudyRoom;
import org.livestudy.domain.studyroom.StudyRoomStatus;
import org.livestudy.exception.CustomException;
import org.livestudy.exception.ErrorCode;
import org.livestudy.repository.RoomRedisRepository;
import org.livestudy.repository.StudyRoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class StudyRoomServiceImpl implements StudyRoomService {

    private final StudyRoomRepository studyRoomRepository;
    private final RoomRedisRepository roomRedisRepository;


    @Override
    public Long enterRoom(String userId) {

        // 중복 입장 방지
        String existingRoomId = roomRedisRepository.getUserRoom(userId);
        if (existingRoomId != null) {
            throw new CustomException(ErrorCode.USER_ALREADY_IN_ROOM);
        }

        List<StudyRoom> studyRooms = studyRoomRepository.findByStatus(StudyRoomStatus.OPEN);

        for(StudyRoom studyRoom : studyRooms) {
            Long currentCount = roomRedisRepository.incrementRoomCount(String.valueOf(studyRoom.getId()));

            if(currentCount <= studyRoom.getCapacity()){
                roomRedisRepository.setUserRoom(userId, String.valueOf(studyRoom.getId()));
                return studyRoom.getId();
            } else {
                roomRedisRepository.decrementRoomCount(String.valueOf(studyRoom.getId()));
            }
        }

        throw new CustomException(ErrorCode.NO_ROOMS_IN_SERVER);
    }



    @Override
    public void leaveRoom(String userId) {
        String roomId = roomRedisRepository.getUserRoom(userId);

        if(roomId != null) {
            roomRedisRepository.decrementRoomCount(roomId);
            roomRedisRepository.deleteUserRoom(userId);
        }
    }

    @Override
    public String createRoom(int capacity) {
        if (capacity <= 0 || capacity > 1000) {
            throw new CustomException(ErrorCode.INVALID_ROOM_CAPACITY);
        }

        String roomId = UUID.randomUUID().toString();

        StudyRoom room = StudyRoom.of(0, capacity, StudyRoomStatus.OPEN);

        studyRoomRepository.save(room);

        roomRedisRepository.setUserRoom(roomId, roomId);
        roomRedisRepository.incrementRoomCount(roomId);


        return roomId;
    }
}
