package com.precapstone.fiveguys_backend.group;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupsRepository groupsRepository;

    public Groups createGroup(GroupCreateParm groupCreateParm) {
        var parentGroup = groupsRepository.findById(groupCreateParm.getParentGroupId())
                .orElse(null);

        var group = Groups.builder()
                .groupsName(groupCreateParm.getGroupName())
                .parent(parentGroup)
                .build();

        groupsRepository.save(group);

        return group;
    }

    public Groups deleteGroup(int groupId) {
        var group = groupsRepository
                .findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group Not Found"));

        groupsRepository.deleteById(groupId);

        return group;
    }

    public Groups updateGroup(GroupPatchParm groupPatchParm) {
        var group = groupsRepository
                .findById(groupPatchParm.getGroupId())
                .orElseThrow(() -> new RuntimeException("Group Not Found"));

        var parentGroup = groupsRepository
                .findById(groupPatchParm.getNewParentGroupId())
                .orElseThrow(() -> new RuntimeException("ParentGroup Not Found"));

        if(groupPatchParm.getNewGroupName() != null) group.setGroupsName(groupPatchParm.getNewGroupName());
        if(groupPatchParm.getNewParentGroupId() != -1) group.setParent(parentGroup);

        groupsRepository.save(group);

        return group;
    }

    public Groups infoById(int groupId) {
        var group = groupsRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group Not Found"));
        return group;
    }

    public Groups infoByName(String groupName) {
        var group = groupsRepository.findByGroupsName(groupName)
                .orElseThrow(() -> new RuntimeException("Group Not Found"));

        return group;
    }

    public List<Groups> childGroupInfo(int groupId) {
        var group = groupsRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group Not Found"));

        var groups = groupsRepository.findByChild(group)
                .orElseThrow(() -> new RuntimeException("Group Not Found"));

        return groups;
    }
}