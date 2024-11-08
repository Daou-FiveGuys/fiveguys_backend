package com.precapstone.fiveguys_backend.group;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;

    public Group createGroup(GroupCreateParm groupCreateParm) {
        var parentGroup = groupRepository.findByGroupName(groupCreateParm.getGroupName())
                .orElseThrow(() -> new RuntimeException("ParentGroup Not Found"));

        var group = Group.builder()
                .groupName(groupCreateParm.getGroupName())
                .parentGroup(parentGroup)
                .build();

        groupRepository.save(group);

        return group;
    }

    public Group deleteGroup(int groupId) {
        var group = groupRepository
                .findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group Not Found"));

        groupRepository.deleteById(groupId);

        return group;
    }

    public Group updateGroup(GroupPatchParm groupPatchParm) {
        var group = groupRepository
                .findById(groupPatchParm.getGroupId())
                .orElseThrow(() -> new RuntimeException("Group Not Found"));

        var parentGroup = groupRepository
                .findById(groupPatchParm.getGroupId())
                .orElseThrow(() -> new RuntimeException("ParentGroup Not Found"));

        if(groupPatchParm.getNewGroupName() != null) group.setGroupName(groupPatchParm.getNewGroupName());
        if(groupPatchParm.getNewParentGroupId() != null) group.setParentGroup(parentGroup);

        groupRepository.save(group);

        return group;
    }

    public Group infoById(int groupId) {
        var group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group Not Found"));
        return group;
    }

    public Group infoByName(String groupName) {
        var group = groupRepository.findByGroupName(groupName)
                .orElseThrow(() -> new RuntimeException("Group Not Found"));

        return group;
    }

    public List<Group> childGroupInfo(int groupId) {
        var group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group Not Found"));

        var groups = groupRepository.findByChildGroup(group)
                .orElseThrow(() -> new RuntimeException("Group Not Found"));

        return groups;
    }
}
