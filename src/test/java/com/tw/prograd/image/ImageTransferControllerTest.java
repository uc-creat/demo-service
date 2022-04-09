package com.tw.prograd.image;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tw.prograd.image.dto.UploadImage;
import com.tw.prograd.image.exception.ImageNotFoundException;
import com.tw.prograd.image.exception.ImageStorageException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayInputStream;

import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest
class ImageTransferControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ImageTransferService service;

    private MockMultipartFile image;

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
        when(service.imageByName("image.png")).thenReturn(resource);
        when(service.contentType(resource)).thenReturn("image/png");

        mvc.perform(get("/images/image.png"))
                .andExpect(status().isOk())
                .andExpect(header().stringValues(CONTENT_TYPE, "image/png"))
                .andExpect(header().stringValues(CONTENT_DISPOSITION, "attachment; filename=\"image.png\""))
                .andExpect(content().bytes(imageContent));

        verify(service).imageByName("image.png");
    }

    @Test
    @Disabled("GET and POST has same url, so url resolver could not resolve to correct route")
    public void shouldNotReturnAnythingWhenImageNameIsNotSent() throws Exception {

        mvc.perform(get("/images"))
                .andExpect(status().isNotFound());

        verifyNoInteractions(service);
    }

    @Test
    public void shouldNotReturnImageWhenNonExistingImageRequested() throws Exception {

        when(service.imageByName("image.png")).thenThrow(ImageNotFoundException.class);

        mvc.perform(get("/images/image.png"))
                .andExpect(status().isNotFound());

        verify(service).imageByName("image.png");
    }

    @Test
    public void shouldSaveImageWhenUploaded() throws Exception {

        UploadImage uploadImage = new UploadImage(1, "image.png");
        when(service.store(image)).thenReturn(uploadImage);

        this.mvc.perform(multipart("/images").file(image))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/images/image.png"))
                .andExpect(content().json(mapper.writeValueAsString(uploadImage)));

        verify(service).store(image);
    }

    @Test
    public void shouldForbiddenTheUploadWhenFailedToStoreImage() throws Exception {

        doThrow(ImageStorageException.class).when(service).store(image);

        this.mvc.perform(multipart("/images").file(image))
                .andExpect(status().isForbidden());

        verify(service).store(image);
    }
}