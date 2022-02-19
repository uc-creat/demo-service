package com.tw.prograd.image;

import com.tw.prograd.image.exception.ImageNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayInputStream;

import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureMockMvc
@SpringBootTest
class ImageTransferControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ImageService service;

    @Test
    public void shouldReturnImageWhenExistingImageRequested() throws Exception {
        Resource resource = mock(Resource.class);
        byte[] imageContent = "dummy image content".getBytes();
        when(resource.getInputStream()).thenReturn(new ByteArrayInputStream(imageContent));
        when(resource.getFilename()).thenReturn("image.png");
        when(service.loadAsResource("image.png")).thenReturn(resource);

        mvc.perform(get("/images/image.png"))
                .andExpect(status().isOk())
                .andExpect(header().stringValues(CONTENT_DISPOSITION, "attachment; image=\"image.png\""))
                .andExpect(content().bytes(imageContent));

        verify(service).loadAsResource("image.png");
    }

    @Test
    public void shouldNotReturnAnythingWhenImageNameIsNotSent() throws Exception {

        mvc.perform(get("/images"))
                .andExpect(status().isNotFound());

        verifyNoInteractions(service);
    }

    @Test
    public void shouldNotReturnImageWhenNonExistingImageRequested() throws Exception {

        when(service.loadAsResource("image.png")).thenThrow(ImageNotFoundException.class);

        mvc.perform(get("/images/image.png"))
                .andExpect(status().isNotFound());

        verify(service).loadAsResource("image.png");
    }
}