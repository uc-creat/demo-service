package com.tw.prograd.image;

import com.tw.prograd.image.DTO.UploadImage;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import static javax.persistence.GenerationType.IDENTITY;

@Entity(name = "image")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ImageEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    public UploadImage toSavedImageDTO() {
        return new UploadImage(id, name);
    }
}
