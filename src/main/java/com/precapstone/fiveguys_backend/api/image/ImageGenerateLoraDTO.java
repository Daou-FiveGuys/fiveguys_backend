package com.precapstone.fiveguys_backend.api.image;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ImageGenerateLoraDTO {
    String prompt;
    String lora;
    int width;
    int height;
    int numInterfaceSteps;
    int seed;
    int cfg;
}
