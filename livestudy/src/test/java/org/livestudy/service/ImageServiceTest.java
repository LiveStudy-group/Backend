package org.livestudy.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.livestudy.service.ImageService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.livestudy.exception.CustomException;
import org.livestudy.exception.ErrorCode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ImageServiceTest {

    @InjectMocks
    private ImageService imageService;

    private String testUploadDir = "target/test-upload-dir";
    private MockMultipartFile mockFile;

    @BeforeEach
    void setUp() throws IOException {
        // 실제 파일 저장 경로를 가상으로 설정
        ReflectionTestUtils.setField(imageService, "uploadDir", testUploadDir);

        // 테스트를 위해 임시 디렉터리 생성 및 MockMultipartFile 객체 생성
        Path testPath = Paths.get(testUploadDir);
        Files.createDirectories(testPath);
        mockFile = new MockMultipartFile(
                "imageFile", "test.jpg", "image/jpeg", "test image content".getBytes());
    }

    @AfterEach
    void tearDown() throws IOException {
        // 테스트 후 생성된 디렉터리 삭제
        Files.walk(Paths.get(testUploadDir))
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }

    @Test
    void uploadImage_success_returnsImageUrl() {
        // when
        String imageUrl = imageService.uploadImage(mockFile);

        // then
        assertThat(imageUrl).contains("/images/").contains("test.jpg");
        // 실제 파일이 저장되었는지 확인
        Path savedFile = Paths.get(testUploadDir, imageUrl.substring("/images/".length()));
        assertThat(Files.exists(savedFile)).isTrue();
    }

    @Test
    void uploadImage_throwsIOException_throwsCustomException() throws IOException {
        // Given
        // IOException을 발생시키기 위해 Files.copy를 Mocking할 수 없으므로,
        // MockMultipartFile의 inputStream에 에러를 주입하거나,
        // @Spy를 이용해 Files.copy를 감시하고 예외를 던지도록 설정할 수 있습니다.
        // 이 예제에서는 간결성을 위해 Mockito의 MockMultipartFile에서 IOException을 시뮬레이션합니다.
        MockMultipartFile fileWithError = mock(MockMultipartFile.class);
        when(fileWithError.getOriginalFilename()).thenReturn("test.jpg");
        when(fileWithError.getInputStream()).thenThrow(new IOException("Test IO Exception"));

        // when & then
        CustomException thrown = assertThrows(CustomException.class, () -> {
            imageService.uploadImage(fileWithError);
        });

        assertThat(thrown.getErrorCode()).isEqualTo(ErrorCode.IMAGE_UPLOAD_FAILED);
    }
}