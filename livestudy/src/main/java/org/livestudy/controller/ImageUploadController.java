package org.livestudy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.livestudy.dto.ErrorResponse;
import org.livestudy.service.ImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/images")
public class ImageUploadController {

    private final ImageService imageService;

    public ImageUploadController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/upload")
    @Operation(summary = "프로필이미지 등록", description = "유저(자신)의 프로필 이미지를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이미지가 성공적으로 등록되었습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "등록할 대상을 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생.")
    })
    public ResponseEntity<String> updateImage(@RequestPart("imageFile") MultipartFile imageFile) {

            String imageUrl = imageService.uploadImage(imageFile);
            return ResponseEntity.ok(imageUrl);
    }
}
