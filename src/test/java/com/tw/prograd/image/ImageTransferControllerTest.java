package com.tw.prograd.image;

import com.tw.prograd.image.exception.ImageNotFoundException;
import com.tw.prograd.image.exception.ImageStoreException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayInputStream;

import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureMockMvc
@SpringBootTest
class ImageTransferControllerTest {

    MockMultipartFile image;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private ImageTransferService service;
    private byte[] imageContent;

    @BeforeEach
    void setUp() {
        imageContent = "dummy image content".getBytes();
        image = new MockMultipartFile("image", "image.png", "multipart/form-data", imageContent);

    }

    @Test
    public void shouldReturnImageWhenExistingImageRequested() throws Exception {

        Resource resource = mock(Resource.class);
        when(resource.getInputStream()).thenReturn(new ByteArrayInputStream(imageContent));
        when(resource.getFilename()).thenReturn("image.png");
        when(service.load("image.png")).thenReturn(resource);

        mvc.perform(get("/images/image.png"))
                .andExpect(status().isOk())
                .andExpect(header().stringValues(CONTENT_DISPOSITION, "attachment; image=\"image.png\""))
                .andExpect(content().bytes(imageContent));

        verify(service).load("image.png");
    }

    @Test
    public void shouldNotReturnAnythingWhenImageNameIsNotSent() throws Exception {

        mvc.perform(get("/images"))
                .andExpect(status().isNotFound());

        verifyNoInteractions(service);
    }

    @Test
    public void shouldNotReturnImageWhenNonExistingImageRequested() throws Exception {

        when(service.load("image.png")).thenThrow(ImageNotFoundException.class);

        mvc.perform(get("/images/image.png"))
                .andExpect(status().isNotFound());

        verify(service).load("image.png");
    }

    @Test
    public void shouldSaveImageWhenUploaded() throws Exception {

        doNothing().when(service).store(image);

        this.mvc.perform(multipart("/").file(image))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/"));

        verify(service).store(image);
    }

    @Test
    public void shouldForbiddenTheUploadWhenFailedToStoreImage() throws Exception {

        doThrow(ImageStoreException.class).when(service).store(image);

        this.mvc.perform(multipart("/").file(image))
                .andExpect(status().isForbidden());

        verify(service).store(image);
    }
}