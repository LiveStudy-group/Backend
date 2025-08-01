package org.livestudy.controller.report;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.livestudy.dto.ErrorResponse;
import org.livestudy.dto.report.ReportRequest;
import org.livestudy.service.report.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "신고 API", description = "유저 신고 접수/처리 API")
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    @Operation(summary = "신고 접수", description = "특정 유저에 대한 신고를 접수합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "신고가 성공적으로 접수되었습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다(자기 자신 신고, 중복 신고 등).",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "신고 대상을 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생.")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "신고 요청 정보",
            required = true,
            content = @Content(schema = @Schema(implementation = ReportRequest.class))
    )
    public ResponseEntity<Void> report(@Valid @RequestBody ReportRequest dto,
                                       @AuthenticationPrincipal(expression = "id") Long userId) {
        reportService.report(dto.toCommand(), userId);
        return ResponseEntity.ok().build();
    }
}