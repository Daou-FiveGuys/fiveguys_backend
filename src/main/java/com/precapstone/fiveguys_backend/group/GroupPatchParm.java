package com.precapstone.fiveguys_backend.group;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GroupPatchParm {
    private int groupId;
    private String newGroupName;
    private String newParentGroupId;
}
