package com.precapstone.fiveguys_backend.group;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GroupPatchParm {
    private Long groupId;
    private String newGroupName;
    private Long newParentGroupId;
}
