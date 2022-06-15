package com.tw.prograd.image.storage.file;

import ch.qos.logback.core.util.FileUtil;
import com.tw.prograd.image.ImageEntity;
import com.tw.prograd.image.ImageRepository;
import com.tw.prograd.image.storage.file.config.StorageProperties;
import com.tw.prograd.image.storage.file.exception.EmptyFileException;
import com.tw.prograd.image.storage.file.exception.ImageNotFoundException;
import com.tw.prograd.image.storage.file.exception.StorageException;
import com.tw.prograd.image.storage.file.exception.StorageInitializeException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static java.nio.file.Paths.get;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.Objects.requireNonNull;

@Service
public class StorageService {

    private final Path rootLocation;

    private final StorageProperties properties;

    private final ImageRepository imageRepository;

    public StorageService(StorageProperties properties,
                          ImageRepository imageRepository) {
        this.properties = properties;
        this.rootLocation = get(properties.getLocation());
        this.imageRepository = imageRepository;
    }

    public void init() {
        if (properties.isInitialize()) {
            try {
                Path directories = Files.createDirectories(rootLocation);
                //TODO :: temp image info initialization until getting permanent image storage
                if (isEmpty(directories)) {
                    imageRepository.deleteAll();
                    ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(this.getClass().getClassLoader());
                    for (Resource resource : resolver.getResources("classpath:images/*")) {
                        copyImage(resource);
                        addImageInfoInImageTable(resource.getFilename());
                    }
                }
            } catch (IOException e) {
                throw new StorageInitializeException("Could not initialize storage", e);
            }
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

            Path path = get(requireNonNull(image.getOriginalFilename()));
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
            return Files.probeContentType(get(image.getURI()));
        } catch (IOException e) {
            throw new StorageException("Failed to read content type for file" + image.getFilename(), e);
        }
    }

    private void addImageInfoInImageTable(String fileName) {
        imageRepository.save(imageEntity(fileName));
    }

    private boolean isEmpty(Path path) throws IOException {
        if (!Files.isDirectory(path))
            return false;

        try (Stream<Path> entries = Files.list(path)) {
            return entries.findFirst().isEmpty();
        }
    }

    private void copyImage(Resource resource) throws IOException {
        Path path = get(rootLocation.toAbsolutePath().toString(), resource.getFilename());

        if (!Files.exists(path)) {
            File file = new File(path.toString());
            FileUtil.createMissingParentDirectories(file);
        }
        FileCopyUtils.copy(resource.getInputStream(), new FileOutputStream(path.toFile()));
    }

    private ImageEntity imageEntity(String imageName) {
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setName(imageName);
        return imageEntity;
    }
}
