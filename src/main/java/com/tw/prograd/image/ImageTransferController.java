package com.tw.prograd.image;

import com.tw.prograd.image.exception.ImageNotFoundException;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

@RestController
public class ImageTransferController {

    private final ImageService service;

    public ImageTransferController(ImageService service) {
        this.service = service;
    }

    @GetMapping("/images/{name:.+}")
    public ResponseEntity<Resource> serveImage(@PathVariable String name) {

        Resource image = service.loadAsResource(name);
        return ResponseEntity
                .ok()
                .header(CONTENT_DISPOSITION, "attachment; image=\"" + image.getFilename() + "\"")
                .body(image);
    }

    @ExceptionHandler(ImageNotFoundException.class)
    private ResponseEntity<?> handleImageNotFoundException(ImageNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }
}
