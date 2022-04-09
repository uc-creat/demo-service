package com.tw.prograd.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.of;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CORSConfigTest {
    @Autowired
    private MockMvc mvc;

    @Test
    public void verifyCORSAllowedDomain() throws Exception {
        this.mvc.perform(options("/api/images1/image.png")
                        .header("Access-Control-Request-Method", GET)
                        .header("Origin", "http://*.notAllowedDomain.app"))
                .andExpect(header().string("Access-Control-Allow-Methods", (String) null))
                .andExpect(header().string("Access-Control-Allow-Origin", (String) null))
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @MethodSource("provideAllowedHTTPMethod")
    public void verifyCORSAllowedHTTPMethod(HttpMethod httpMethod, HttpStatus status) throws Exception {

        mvc.perform(options("/api/images1/image.png")
                        .header("Access-Control-Request-Method", httpMethod.name())
                        .header("Origin", "http://*.netlify.app"))
                .andExpect(header().string("Access-Control-Allow-Methods", "POST,GET"))
                .andExpect(header().string("Access-Control-Allow-Origin", "http://*.netlify.app"))
                .andExpect(status().is(status.value()));
    }

    @ParameterizedTest
    @MethodSource("provideNotAllowedHTTPMethod")
    public void verifyCORSNotAllowedHTTPMethod(HttpMethod httpMethod, HttpStatus status) throws Exception {
        mvc.perform(options("/api/images1/image.png")
                        .header("Access-Control-Request-Method", httpMethod.name())
                        .header("Origin", "http://*.netlify.app"))
                .andExpect(header().string("Access-Control-Allow-Methods", (String) null))
                .andExpect(header().string("Access-Control-Allow-Origin", (String) null))
                .andExpect(status().is(status.value()));
    }

    private static Stream<Arguments> provideAllowedHTTPMethod() {
        return Map.of(
                        GET, OK,
                        POST, OK)
                .entrySet().stream().map(t -> of(t.getKey(), t.getValue()));
    }

    private static Stream<Arguments> provideNotAllowedHTTPMethod() {
        return Map.of(
                        HEAD, FORBIDDEN,
                        PUT, FORBIDDEN,
                        PATCH, FORBIDDEN,
                        DELETE, FORBIDDEN,
                        OPTIONS, FORBIDDEN)
                .entrySet().stream().map(t -> of(t.getKey(), t.getValue()));
    }
}