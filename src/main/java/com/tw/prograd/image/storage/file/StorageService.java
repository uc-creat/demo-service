package com.tw.prograd.image.storage.file;

import com.tw.prograd.image.ImageTransferService;
import com.tw.prograd.image.storage.file.config.StorageProperties;
import com.tw.prograd.image.storage.file.exception.EmptyFileException;
import com.tw.prograd.image.storage.file.exception.ImageNotFoundException;
import com.tw.prograd.image.storage.file.exception.StorageException;
import com.tw.prograd.image.storage.file.exception.StorageInitializeException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
public class StorageService implements ImageTransferService {

    private final Path rootLocation;

    public StorageService(StorageProperties properties) {
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
        try {
            Path file = rootLocation.resolve(imageName);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new ImageNotFoundException("Could not read image: " + imageName);
            }

        } catch (MalformedURLException e) {
            throw new ImageNotFoundException("Could not read image: " + imageName);
        }
    }

    @Override
    public void store(MultipartFile image) {
        try {
            if (image.isEmpty())
                throw new EmptyFileException("Failed to store empty file." + image.getOriginalFilename());

            Path path = Paths.get(Objects.requireNonNull(image.getOriginalFilename()));
            Path destinationFile = this.rootLocation.resolve(path).normalize().toAbsolutePath();

            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath()))
                throw new StorageException("Cannot store file outside current directory.");

            try (InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}