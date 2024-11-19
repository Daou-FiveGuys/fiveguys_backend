package com.precapstone.fiveguys_backend.api.group2;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class Group2CreateDTO {
    private Long folder2Id;
    private String name;
}
