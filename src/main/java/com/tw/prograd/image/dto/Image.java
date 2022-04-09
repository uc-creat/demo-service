package com.tw.prograd.image.dto;

import lombok.*;

@Builder
@Data
public class Image {
    private Integer id;
    private String name;
    private String url;
}
