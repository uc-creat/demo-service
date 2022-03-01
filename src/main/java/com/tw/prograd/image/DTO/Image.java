package com.tw.prograd.image.DTO;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Getter
@Setter
public class Image {
    private Integer id;
    private String name;
    private String url;
}
