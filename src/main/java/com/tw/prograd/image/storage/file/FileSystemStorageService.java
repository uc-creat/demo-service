package com.tw.prograd.image.storage.file;

import com.tw.prograd.image.ImageTransferService;
import com.tw.prograd.image.storage.exception.StorageInitializeException;
import com.tw.prograd.image.storage.file.config.StorageProperties;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class FileSystemStorageService implements ImageTransferService {

    private final Path rootLocation;

    public FileSystemStorageService(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation());
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new StorageInitializeException("Could not initialize storage", e);
        }
    }

    @Override
    public Resource load(String imageName) {
        return null;
    }

    @Override
    public void store(MultipartFile image) {
        Path destinationFile = this.rootLocation.resolve(
                        Paths.get(Objects.requireNonNull(image.getOriginalFilename())))
                .normalize().toAbsolutePath();
    }

}
