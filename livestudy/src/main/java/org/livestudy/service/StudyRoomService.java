package org.livestudy.service;

public interface StudyRoomService {

    Long enterRoom(String userId); // 입장

    void leaveRoom(String userId);  // 퇴장

    String createRoom(int capacity); // 방 생성

}
