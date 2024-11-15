package com.precapstone.fiveguys_backend.api.folder2;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Folder2CreateDTO {
    private Long userId;
    private String name;
}
