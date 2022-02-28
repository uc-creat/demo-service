package com.tw.prograd.image;

import com.tw.prograd.image.DTO.UploadImage;
import com.tw.prograd.image.exception.ImageNotFoundException;
import com.tw.prograd.image.exception.ImageStorageException;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URI;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("/images")
public class ImageTransferController {

    @Value("#{servletContext.contextPath}")
    private String servletContextPath;

    private final ImageTransferService service;

    public ImageTransferController(ImageTransferService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> serveImage(@PathVariable Integer id) {

        Resource image = service.load(id);

        return status(OK)
                .header(CONTENT_DISPOSITION, "attachment; image=\"" + image.getFilename() + "\"")
                .body(image);
    }

    @PostMapping(consumes = MULTIPART_FORM_DATA_VALUE, produces = "application/json")
    public ResponseEntity<UploadImage> uploadImage(@RequestPart("image") MultipartFile file, RedirectAttributes redirectAttributes) {

        UploadImage image = service.store(file);
        redirectAttributes.addFlashAttribute("message", "You successfully uploaded " + image.getName() + "!");

        return status(FOUND)
                .location(URI.create(servletContextPath + "/images/" + image.getId()))
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
