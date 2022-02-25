package com.tw.prograd.image.storage.file;

import com.tw.prograd.image.storage.file.config.StorageProperties;
import com.tw.prograd.image.storage.file.exception.EmptyFileException;
import com.tw.prograd.image.storage.file.exception.StorageException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

class StorageServiceTest {

    private final StorageProperties properties = new StorageProperties();

    private StorageService service;

    @BeforeEach
    public void setup() {
        properties.setLocation("build/files/" + Math.abs(new Random().nextLong()));
        service = new StorageService(properties);
        service.init();
    }

    @Test
    void shouldImageStorageDirectoryBeCreatedWhenApplicationStarted() {
        assertTrue(Files.exists(Paths.get(properties.getLocation())));
    }

    @Test
    public void shouldStoreImageWhenProvided() {
        service.store(new MockMultipartFile("foo", "foo.png", IMAGE_PNG_VALUE, "Hello, World".getBytes()));
        assertThat(Paths.get(properties.getLocation()).resolve("foo.png")).exists();
    }

    @Test
    public void shouldNotStoreImageWhenGivenImageHasNoContent() {
        assertThrows(EmptyFileException.class, () -> service.store(new MockMultipartFile("foo", "foo.png", IMAGE_PNG_VALUE, new byte[0])));
    }

    @Test
    public void saveAbsolutePathNotPermitted() {
        assertThrows(StorageException.class, () -> service.store(new MockMultipartFile("foo", "/etc/passwd",
                IMAGE_PNG_VALUE, "Hello, World".getBytes())));
    }
}