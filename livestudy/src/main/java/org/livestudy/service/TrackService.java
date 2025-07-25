package org.livestudy.service;

import org.livestudy.domain.TrackType.TrackType;

import java.util.Map;

public interface TrackService {

    void saveTrack(String userId, String trackSid, TrackType type); // 트랙 저장

    void removeTrack(String trackSid); // 트랙 삭제

    Map<Object, Object> getTracksByUser(String userId); // User의 트랙 목록 조회
}
