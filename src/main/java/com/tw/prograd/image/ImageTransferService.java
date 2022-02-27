package com.tw.prograd.image;

import com.tw.prograd.image.DTO.UploadImage;
import com.tw.prograd.image.storage.file.StorageService;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageTransferService {

    private StorageService service;

    private ImageRepository repository;

    public ImageTransferService(StorageService service, ImageRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    Resource load(String imageName) {
        return service.load(imageName);
    }

    UploadImage store(MultipartFile file) {
        service.store(file);
        ImageEntity imageEntity = new ImageEntity(null, file.getOriginalFilename());
        return repository.save(imageEntity).toSavedImageDTO();
    }
}
