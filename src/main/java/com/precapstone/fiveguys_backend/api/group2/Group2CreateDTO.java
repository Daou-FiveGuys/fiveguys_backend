package com.precapstone.fiveguys_backend.api.group2;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Group2CreateDTO {
    private Long groupId;
    private String name;
}
