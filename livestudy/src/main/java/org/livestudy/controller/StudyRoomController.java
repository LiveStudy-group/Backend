package org.livestudy.controller;


import lombok.RequiredArgsConstructor;
import org.livestudy.service.StudyRoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/study-rooms")
@RequiredArgsConstructor
public class StudyRoomController {

    private final StudyRoomService studyRoomService;

    @PostMapping("/enter")
    public ResponseEntity<String> enterRoom(@RequestParam String userId){
        String roomId = String.valueOf(studyRoomService.enterRoom(userId));
        return ResponseEntity.ok(roomId);
    }

    @PostMapping("/leave")
    public ResponseEntity<String> leaveRoom(@RequestParam String userId){
        studyRoomService.leaveRoom(userId);
        return ResponseEntity.ok().build();
    }
}
