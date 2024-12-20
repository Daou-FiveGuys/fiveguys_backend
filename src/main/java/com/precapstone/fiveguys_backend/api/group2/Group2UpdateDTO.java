package com.precapstone.fiveguys_backend.api.group2;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class Group2UpdateDTO {
    private Long group2Id;
    private String folder2Id;
    private String name;
}
