package com.precapstone.fiveguys_backend.api.group;

import com.precapstone.fiveguys_backend.entity.Groups;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Builder
public class GroupsResponse {
    private Groups group;
    private List<Groups> childGroups;
}
