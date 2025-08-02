package org.livestudy.controller;

import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.livestudy.domain.title.Title;
import org.livestudy.domain.user.User;
import org.livestudy.dto.GrantTitleRequest;
import org.livestudy.dto.GrantTitleResponse;
import org.livestudy.dto.UserTitleResponse;
import org.livestudy.service.TitleService;
import org.livestudy.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/titles")
@RequiredArgsConstructor
@Tag(name = "칭호 & 뱃지", description = "칭호 & 뱃지 관련 API")
public class TitleController {

    private final TitleService titleService;
    private final UserService userService;

    @PostMapping("/evaluate")
    @Operation(summary = "획득 가능한 칭호 평가 및 지급")
    public ResponseEntity<GrantTitleResponse> grantTitles(@RequestBody GrantTitleRequest request) {
        User user = userService.getUserById(request.userId());
        List<Title> granted = titleService.evaluateAndGrantTitles(user.getId());
        return ResponseEntity.ok(new GrantTitleResponse(
                granted.stream().map(Title::getName).toList()
        ));
    }

    @Operation(
            summary = "대표 칭호 설정",
            description = "획득한 칭호 중 하나를 대표 칭호로 설정합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "대표 칭호가 설정되었습니다."),
                    @ApiResponse(responseCode = "400", description = "획득하지 않은 칭호를 대표 칭호로 설정할 수 없습니다."),
                    @ApiResponse(responseCode = "404", description = "칭호를 찾을 수 없습니다.")
            }
    )
    @PostMapping("/{userId}/equip")
    public ResponseEntity<UserTitleResponse> equipTitle(
            @Parameter(description = "사용자 ID", example = "1")@RequestParam Long titleId,
            @Parameter(description = "장착할 칭호 ID", example = "5")@PathVariable Long userId
    ) {
        UserTitleResponse response = titleService.equipTitle(userId, titleId);
        return ResponseEntity.ok(response);
    }

}
