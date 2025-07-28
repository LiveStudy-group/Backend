package org.livestudy.controller.report;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<Void> report(@Valid @RequestBody ReportRequest dto,
                                 @AuthenticationPrincipal(expression = "id") Long userId) {
        reportService.report(dto.toCommand(), userId);
        return ResponseEntity.ok().build();
    }
}
