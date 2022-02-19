package com.tw.prograd.image;

import org.springframework.core.io.Resource;

public interface ImageService {

    Resource loadAsResource(String imageName);
}
