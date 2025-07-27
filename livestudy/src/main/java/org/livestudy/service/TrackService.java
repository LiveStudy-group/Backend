package org.livestudy.service;

import org.livestudy.domain.TrackType.TrackType;

import java.util.Map;

public interface TrackService {

    void saveTrack(String userId, String trackSid, TrackType type); // 트랙 저장

    void removeTrack(String trackSid, TrackType type); // 특정 타입의 트랙 삭제

    Map<Object, Object> getTracksByUser(String userId); // User의 트랙 목록 조회

    void switchTrack(String userId, String trackSid, TrackType type); // 트랙의 정보 갱신 및 변경

    String getTrack(String userId, TrackType type); // 특정 타입의 Track을 조회한다.

    Map<String, String> getAllTracks(String userId); // 사용자가 갖는 Track 모두를 조회한다.

    void removeAllTracks(String userId); // 모든 트랙 삭제
}
