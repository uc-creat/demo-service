package com.tw.prograd.image;

import com.tw.prograd.image.DTO.UploadImage;
import com.tw.prograd.image.storage.file.StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageTransferServiceTest {

    @Mock
    private StorageService storageService;

    @Mock
    private ImageRepository imageRepository;

    @InjectMocks
    private ImageTransferService imageTransferService;

    private MockMultipartFile image;

    private byte[] imageContent;

    @BeforeEach
    void setUp() {
        imageContent = "dummy image content".getBytes();
        image = new MockMultipartFile("image", "image.png", "multipart/form-data", imageContent);
    }

    @Test
    void shouldStoreImage() {
        doNothing().when(storageService).store(image);
        ImageEntity entity = new ImageEntity(null, "image.png");
        ImageEntity savedImage = new ImageEntity(1, "image.png");
        when(imageRepository.save(any())).thenReturn(savedImage);

        UploadImage actual = imageTransferService.store(image);

        assertEquals(new UploadImage(1, "image.png"), actual);
        verify(storageService).store(image);
        verify(imageRepository).save(any());
    }
}