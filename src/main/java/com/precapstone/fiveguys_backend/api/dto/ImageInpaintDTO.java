package com.precapstone.fiveguys_backend.api.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ImageInpaintDTO<T> {
    private Long imageId;
    private String prompt;
    private T mask;
}
