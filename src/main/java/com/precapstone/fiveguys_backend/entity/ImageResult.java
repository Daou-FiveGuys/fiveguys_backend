package com.precapstone.fiveguys_backend.entity;


import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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

    @NotNull
    private String originalRequestId;
    private String userId;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL)
    private ImageInfo originalImageInfo;

    @Nullable
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
