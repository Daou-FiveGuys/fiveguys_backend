package com.precapstone.fiveguys_backend.api.folder2;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class Folder2UpdateDTO {
    private Long folder2Id;
    private String name;
}
