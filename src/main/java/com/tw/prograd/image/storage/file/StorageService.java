package com.tw.prograd.image.storage.file;

import com.tw.prograd.image.ImageEntity;
import com.tw.prograd.image.ImageRepository;
import com.tw.prograd.image.storage.file.config.StorageProperties;
import com.tw.prograd.image.storage.file.exception.EmptyFileException;
import com.tw.prograd.image.storage.file.exception.ImageNotFoundException;
import com.tw.prograd.image.storage.file.exception.StorageException;
import com.tw.prograd.image.storage.file.exception.StorageInitializeException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static java.nio.file.Files.move;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.Arrays.stream;
import static java.util.Objects.requireNonNull;

@Service
public class StorageService {

    private final Path rootLocation;

    private ResourceLoader resourceLoader;

    private ImageRepository imageRepository;

    public StorageService(StorageProperties properties,
                          ResourceLoader resourceLoader,
                          ImageRepository imageRepository) {
        this.rootLocation = Paths.get(properties.getLocation());
        this.resourceLoader = resourceLoader;
        this.imageRepository = imageRepository;
    }


    public void init() {
        try {

            Path directories = Files.createDirectories(rootLocation);
            if (isEmpty(directories)) {
                //TODO :: temp image info initialization until getting permanent image storage
                File file = resourceLoader.getResource("classpath:images").getFile();
                addImageInfoInImageTable(file);
                move(Paths.get(file.getPath()), rootLocation, REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new StorageInitializeException("Could not initialize storage", e);
        }

    }

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

    public void store(MultipartFile image) {
        try {
            if (image.isEmpty())
                throw new EmptyFileException("Failed to store empty file." + image.getOriginalFilename());

            Path path = Paths.get(requireNonNull(image.getOriginalFilename()));
            Path destinationFile = this.rootLocation.resolve(path).normalize().toAbsolutePath();

            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath()))
                throw new StorageException("Cannot store file outside current directory.");

            try (InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, destinationFile, REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new StorageException("Failed to store file" + image.getOriginalFilename(), e);
        }
    }

    public String contentType(Resource image) {
        try {
            return Files.probeContentType(Paths.get(image.getURI()));
        } catch (IOException e) {
            throw new StorageException("Failed to read content type for file" + image.getFilename(), e);
        }
    }

    private void addImageInfoInImageTable(File file) {
        imageRepository.deleteAll();
        stream(requireNonNull(file.list()))
                .map(this::imageEntity)
                .forEach(imageRepository::save);
    }

    private boolean isEmpty(Path path) throws IOException {
        if (!Files.isDirectory(path))
            return false;

        try (Stream<Path> entries = Files.list(path)) {
            return entries.findFirst().isEmpty();
        }
    }

    private ImageEntity imageEntity(String imageName) {
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setName(imageName);
        return imageEntity;
    }
}
