package com.tw.prograd.image;

import com.tw.prograd.image.exception.ImageNotFoundException;
import com.tw.prograd.image.exception.ImageStoreException;
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

    @GetMapping("/images/{name:.+}")
    public ResponseEntity<Resource> serveImage(@PathVariable String name) {

        Resource image = service.loadAsResource(name);

        return status(OK)
                .header(CONTENT_DISPOSITION, "attachment; image=\"" + image.getFilename() + "\"")
                .body(image);
    }

    @PostMapping("/")
    public ResponseEntity<Void> handleFileUpload(@RequestParam("image") MultipartFile image, RedirectAttributes redirectAttributes) {

        service.store(image);
        redirectAttributes.addFlashAttribute("message", "You successfully uploaded " + image.getOriginalFilename() + "!");

        return status(FOUND)
                .location(URI.create("/")).build();
    }

    @ExceptionHandler(ImageNotFoundException.class)
    private ResponseEntity<?> handleImageNotFoundException(ImageNotFoundException exception) {

        return status(NOT_FOUND).build();
    }

    @ExceptionHandler(ImageStoreException.class)
    private ResponseEntity<?> handleImageStoreException(ImageStoreException exception) {

        return status(FORBIDDEN).build();
    }
}
