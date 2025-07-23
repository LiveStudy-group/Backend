package org.livestudy.service;

import org.junit.jupiter.api.AfterEach;
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
import org.springframework.test.context.ActiveProfiles;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
public class StudyRoomServiceTest {

    @Autowired
    private StudyRoomRepository studyRoomRepository;

    @Autowired
    private RoomRedisRepository roomRedisRepository;

    @Autowired
    private StudyRoomServiceImpl studyRoomService;

    private final String testUserId = "user123";

    private final String userId = "user456";

    @BeforeEach
    void setUp() {
        // 테스트용 방 생성 (정원 20)
        studyRoomRepository.deleteAll();
        StudyRoom room = StudyRoom.of(4, 20, StudyRoomStatus.OPEN);
        studyRoomRepository.save(room);
        clearRedis();
    }

    @AfterEach
    void tearDown() {
        for (String uid : List.of(testUserId, userId)) {
            String roomId = roomRedisRepository.getUserRoom(uid);
            if (roomId != null) {
                roomRedisRepository.decrementRoomCount(roomId);
                roomRedisRepository.deleteUserRoom(uid);
            }
        }

        clearRedis();
    }

    private void clearRedis(){
        roomRedisRepository.deleteUserRoom(testUserId);
        roomRedisRepository.decrementRoomCount(testUserId);
        roomRedisRepository.deleteUserRoom(userId);
        roomRedisRepository.decrementRoomCount(userId);
    }

    @Test
    void enterRoom_success_assignToNewRoom_whenNoOpenRoom() {
        // when
        Long roomId = studyRoomService.enterRoom(userId);

        // then
        assertNotNull(roomId);
        assertEquals(roomId.toString(), roomRedisRepository.getUserRoom(userId));
    }

    @Test
    void enterRoom_success_assignToExistingRoom_whenAvailable() {
        // given
        StudyRoom room = StudyRoom.of(3, 20, StudyRoomStatus.OPEN);
        studyRoomRepository.save(room);

        // when
        Long roomId = studyRoomService.enterRoom(userId);

        // then
        assertEquals(room.getId(), roomId);

        System.out.println("예상 ID : " + room.getId());
        System.out.println("실제 ID : " + roomId);
    }

    @Test
    void enterRoom_fail_whenUserAlreadyInRoom() {
        // given
        studyRoomService.enterRoom(userId);

        // when & then
        CustomException ex = assertThrows(CustomException.class, () -> {
            studyRoomService.enterRoom(userId);
        });

        assertEquals(ErrorCode.USER_ALREADY_IN_ROOM, ex.getErrorCode());
    }

    @Test
    void enterRoom_createNewRoom_whenAllRoomsFull() {
        // given: 이미 정원 20명인 방
        StudyRoom fullRoom = StudyRoom.of(20, 20, StudyRoomStatus.OPEN);
        studyRoomRepository.save(fullRoom);

        // when
        Long newRoomId = studyRoomService.enterRoom(userId);

        // then
        assertNotEquals(fullRoom.getId(), newRoomId);  // 새로운 방이어야 함
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

    @Test
    void  createRoom_fail_whenRedisUnavailable() {
        int validCapacity = 20;

        CustomException exception = assertThrows(
                CustomException.class,
                () -> studyRoomService.createRoom(validCapacity)
        );

        assertEquals(ErrorCode.REDIS_CONNECTION_FAILED, exception.getErrorCode());
    }



}
