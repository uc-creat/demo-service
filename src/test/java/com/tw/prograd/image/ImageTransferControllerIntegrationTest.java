package com.tw.prograd.image;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class ImageTransferControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void shouldImagesRouteAllowedInCORSFilter() throws Exception {
        this.mvc.perform(options("/images/image.png")
                        .header("Access-Control-Request-Method", "GET")
                        .header("Origin", "http://www.someurl.com"))
                .andExpect(status().isOk());
    }
}