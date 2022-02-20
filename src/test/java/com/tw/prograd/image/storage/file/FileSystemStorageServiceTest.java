package com.tw.prograd.image.storage.file;

import com.tw.prograd.image.storage.file.config.StorageProperties;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileSystemStorageServiceTest {

    private StorageProperties properties = new StorageProperties();

    private FileSystemStorageService service;

    @BeforeEach
    public void setup() {
        properties.setLocation("build/files/" + Math.abs(new Random().nextLong()));
        service = new FileSystemStorageService(properties);
        service.init();
    }

    @Test
    void shouldImageStorageDirectoryBeCreatedWhenApplicationStarted() {
        assertTrue(Files.exists(Paths.get(properties.getLocation())));
    }

    @Test
    public void saveAndLoad() {
        service.store(new MockMultipartFile("foo", "foo.txt", MediaType.TEXT_PLAIN_VALUE,
                "Hello, World".getBytes()));
        assertThat(Paths.get(properties.getLocation()).resolve("foo.txt")).exists();
    }
}