package org.livestudy.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "스터디룸 API", description = "스터디룸 입장/퇴장 API")
public class StudyRoomController {

    private final StudyRoomService studyRoomService;

    @PostMapping("/enter")
    @Operation(
            summary = "스터디룸 입장",
            description = "사용자 ID를 기반으로 StudyRoom에 입장하고, 배정된 방 ID를 반환합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "입장 성공, 방 ID 반환"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<String> enterRoom(
            @Parameter(description = "사용자 ID", example = "user123")
            @RequestParam String userId){
        String roomId = String.valueOf(studyRoomService.enterRoom(userId));
        return ResponseEntity.ok(roomId);
    }

    @PostMapping("/leave")
    @Operation(
            summary = "스터디룸 퇴장",
            description = "사용자 ID를 기반으로 StudyRoom에서 퇴장합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "퇴장 성공"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<String> leaveRoom(
            @Parameter(description = "사용자 ID", example = "user123")
            @RequestParam String userId){
        studyRoomService.leaveRoom(userId);
        return ResponseEntity.ok().build();
    }
}
