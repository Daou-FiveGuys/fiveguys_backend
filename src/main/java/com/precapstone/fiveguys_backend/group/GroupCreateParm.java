package com.precapstone.fiveguys_backend.group;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GroupCreateParm {
    private String groupName;
    private String parentGroupId;
}
