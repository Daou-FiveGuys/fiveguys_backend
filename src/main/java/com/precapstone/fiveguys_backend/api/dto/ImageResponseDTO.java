package com.precapstone.fiveguys_backend.api.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
public class ImageResponseDTO{
    String requestId;
    String url;
}
