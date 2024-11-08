package com.precapstone.fiveguys_backend.entity;


import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;

    private String originalRequestId;
    private String userId;

    @OneToOne(cascade = CascadeType.ALL)
    private ImageInfo originalImageInfo;

    @OneToOne(cascade = CascadeType.ALL)
    private ImageInfo editedImageInfo;


    public ImageResult setOriginalImageInfo(ImageInfo originalImageInfo) {
        this.originalImageInfo = originalImageInfo;
        return this;
    }

    public ImageResult setEditedImageInfo(ImageInfo editedImageInfo) {
        this.editedImageInfo = editedImageInfo;
        return this;
    }
}
