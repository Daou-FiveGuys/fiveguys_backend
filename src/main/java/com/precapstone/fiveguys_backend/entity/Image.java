package com.precapstone.fiveguys_backend.entity;


import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;
    private String userId;
    private String requestId;
    @Column(columnDefinition = "TEXT")
    private String originalImageLink;
    @Setter
    @Nullable
    private String editedImageLink;

    public Image setOriginalImageLink(String bucketUrl) {
        this.originalImageLink = bucketUrl;
        return this;
    }
}
