package com.tw.prograd.image;

import com.tw.prograd.image.DTO.UploadImage;
import com.tw.prograd.image.exception.ImageNotFoundException;
import com.tw.prograd.image.exception.ImageStorageException;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URI;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.ResponseEntity.status;

@RestController
public class ImageTransferController {

    private final ImageTransferService service;

    public ImageTransferController(ImageTransferService service) {
        this.service = service;
    }

    @GetMapping("/images/{id}")
    public ResponseEntity<Resource> serveImage(@PathVariable Integer id) {

        Resource image = service.load(id);

        return status(OK)
                .header(CONTENT_DISPOSITION, "attachment; image=\"" + image.getFilename() + "\"")
                .body(image);
    }

    @PostMapping("/images")
    public ResponseEntity<UploadImage> uploadImage(@RequestParam("image") MultipartFile file, RedirectAttributes redirectAttributes) {

        UploadImage image = service.store(file);
        redirectAttributes.addFlashAttribute("message", "You successfully uploaded " + image.getName() + "!");

        return status(FOUND)
                .location(URI.create("/images/" + image.getId()))
                .body(image);
    }

    @ExceptionHandler(ImageNotFoundException.class)
    private ResponseEntity<?> handleImageNotFoundException(ImageNotFoundException exception) {

        return status(NOT_FOUND).build();
    }

    @ExceptionHandler(ImageStorageException.class)
    private ResponseEntity<?> handleImageStoreException(ImageStorageException exception) {

        return status(FORBIDDEN).build();
    }
}
