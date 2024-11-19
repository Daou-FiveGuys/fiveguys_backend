package com.precapstone.fiveguys_backend.api.image;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ImageGenerateDTO {
    String prompt;
    String lora;
    String imageSize;
    int numInterfaceSteps;
    int seed;
    int cfg;
}
