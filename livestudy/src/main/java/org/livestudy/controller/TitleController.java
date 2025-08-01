package org.livestudy.controller;

import io.swagger.v3.oas.annotations.Operation;

import lombok.RequiredArgsConstructor;

import org.livestudy.domain.title.Title;
import org.livestudy.domain.user.User;
import org.livestudy.dto.GrantTitleRequest;
import org.livestudy.dto.GrantTitleResponse;
import org.livestudy.service.TitleService;
import org.livestudy.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/titles")
@RequiredArgsConstructor
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
}
