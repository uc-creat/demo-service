package com.tw.prograd.image;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface ImageTransferService {

    Resource loadAsResource(String imageName);

    void store(MultipartFile image);
}
