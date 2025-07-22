package org.livestudy.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.livestudy.domain.studyroom.StudyRoom;
import org.livestudy.domain.studyroom.StudyRoomStatus;
import org.livestudy.exception.CustomException;
import org.livestudy.exception.ErrorCode;
import org.livestudy.repository.RoomRedisRepository;
import org.livestudy.repository.StudyRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class StudyRoomServiceTest {

    @Autowired
    private StudyRoomRepository studyRoomRepository;

    @Autowired
    private RoomRedisRepository roomRedisRepository;

    @Autowired
    private StudyRoomServiceImpl studyRoomService;

    private final String testUserId = "user123";

    @BeforeEach
    void setUp() {
        // 테스트용 방 생성 (정원 500)
        StudyRoom room = StudyRoom.of(0, 500, StudyRoomStatus.OPEN);
        studyRoomRepository.save(room);
    }

    @AfterEach
    void tearDown() {
        // Redis 데이터 삭제
        String roomId = roomRedisRepository.getUserRoom(testUserId);
        if (roomId != null) {
            roomRedisRepository.decrementRoomCount(String.valueOf(Long.valueOf(roomId)));
            roomRedisRepository.deleteUserRoom(testUserId);
        }
    }

    @Test
    void enterRoom_success() {
        // when
        Long enteredRoomId = studyRoomService.enterRoom(testUserId);

        // then
        assertNotNull(enteredRoomId);

        String userRoom = roomRedisRepository.getUserRoom(testUserId);
        Assertions.assertEquals(enteredRoomId.toString(), userRoom);
    }

    @Test
    void enterRoom_failed_AlreadyInRoom() {
        // given
        studyRoomService.enterRoom(testUserId);

        // when & then
        CustomException exception = assertThrows(
                CustomException.class,
                () -> studyRoomService.enterRoom(testUserId)
        );

        Assertions.assertEquals(ErrorCode.USER_ALREADY_IN_ROOM, exception.getErrorCode());
    }

    @Test
    void leaveRoom_success() {
        // given
        Long roomId = studyRoomService.enterRoom(testUserId);

        // when
        studyRoomService.leaveRoom(testUserId);

        // then
        String roomAfterLeave = roomRedisRepository.getUserRoom(testUserId);
        Assertions.assertNull(roomAfterLeave);
    }

    @Test
    void createRoom_success() {
        int capacity = 500;
        String result = studyRoomService.createRoom(capacity);

        assertNotNull(result);
    }

    // ✅ 1. 유효하지 않은 capacity일 때
    @Test
    void createRoom_fail_whenCapacityIsZero() {
        int invalidCapacity = 0;

        CustomException exception = assertThrows(
                CustomException.class,
                () -> studyRoomService.createRoom(invalidCapacity)
        );

        assertEquals(ErrorCode.INVALID_ROOM_CAPACITY, exception.getErrorCode());
    }



}
