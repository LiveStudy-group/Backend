package org.livestudy.service;

import jakarta.annotation.PostConstruct;
import org.livestudy.exception.CustomException;
import org.livestudy.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    //저장할 폴더 생성
    @PostConstruct
    public void init() {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory!", e);
        }
    }

    public String uploadImage(MultipartFile imageFile) {

        try {
            //이미지 파일이 비어있는지 확인
            if (imageFile.isEmpty()) throw new CustomException(ErrorCode.IMAGE_UPLOAD_FAILED);

            // 이미지 타입인지 확인
            String contentType = imageFile.getContentType();
            if (contentType == null || !contentType.startsWith("image/"))
                throw new CustomException(ErrorCode.IMAGE_UPLOAD_FAILED);

            // 경로 점검
            String original = org.springframework.util.StringUtils.cleanPath(
                    java.util.Objects.requireNonNull(imageFile.getOriginalFilename()));
            if (original.contains("..")) throw new CustomException(ErrorCode.IMAGE_UPLOAD_FAILED);

            // 확장자 추출 및 허용 가능한 타입인지 확인
            String ext = original.lastIndexOf('.') > -1 ?
                    original.substring(original.lastIndexOf('.')).toLowerCase() : "";
            if (!java.util.List.of(".png", ".jpg", ".jpeg", ".webp").contains(ext))
                throw new CustomException(ErrorCode.IMAGE_UPLOAD_FAILED);

            String fileName = java.util.UUID.randomUUID() + ext;
            Path filePath = Paths.get(uploadDir, fileName);
            Files.copy(imageFile.getInputStream(), filePath);

            return "/images/" + fileName;

        } catch (IOException e) {
            throw new CustomException(ErrorCode.IMAGE_UPLOAD_FAILED);
        }
    }

}
