package com.precapstone.fiveguys_backend.api.group;

import com.precapstone.fiveguys_backend.api.dto.groups.GroupsCreateDTO;
import com.precapstone.fiveguys_backend.api.dto.groups.GroupsPatchDTO;
import com.precapstone.fiveguys_backend.entity.Groups;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupsRepository groupsRepository;

    /**
     * 그룹을 생성하는 함수
     * 
     * @param groupsCreateDTO 그룹 생성 정보
     * @return
     */
    public Groups createGroup(GroupsCreateDTO groupsCreateDTO) {
        // 부모그룹ID를 통해 추가할 부모 그룹의 정보를 조회한다.
        var parentGroup = groupsRepository.findById(groupsCreateDTO.getParentGroupId())
                .orElse(null);

        // TODO: 그룹명에 대해, 예외처리할 것
        // 그룹 생성
        var group = Groups.builder()
                .groupsName(groupsCreateDTO.getGroupName())
                .parent(parentGroup)
                .build();

        // 그룹 저장
        groupsRepository.save(group);

        return group;
    }

    /**
     * 그룹ID를 통해, 그룹을 조회한다.
     *
     * @param groupId 조회할 그룹ID
     * @return
     */
    public Groups infoByGroupId(Long groupId) {
        var group = groupsRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group Not Found"));
        return group;
    }

    /**
     * 그룹명을 통해, 그룹을 조회한다.
     *
     * @param groupName 조회할 그룹명
     * @return
     */
    public Groups infoByName(String groupName) {
        var group = groupsRepository.findByGroupsName(groupName)
                .orElseThrow(() -> new RuntimeException("Group Not Found"));

        return group;
    }

    /**
     * 그룹ID를 통해 본인의 하위 그룹을 조회한다.
     *
     *
     * @param groupId
     * @return
     */
    public List<Groups> childGroupInfo(Long groupId) {
        var group = groupsRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group Not Found"));

        var groups = groupsRepository.findByParent(group)
                .orElseThrow(() -> new RuntimeException("Group Not Found"));

        return groups;
    }

    /**
     * 그룹을 삭제하는 함수
     *
     * @param groupId 삭제할 그룹ID
     * @return
     */
    public Groups deleteGroup(Long groupId) {
        var group = groupsRepository
                .findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group Not Found"));

        groupsRepository.deleteById(groupId);

        return group;
    }

    /**
     * 그룹을 수정하는 함수
     *
     * @param groupsPatchDTO 그룹 변경 정보
     * @return
     */
    public Groups updateGroup(GroupsPatchDTO groupsPatchDTO) {
        var group = groupsRepository
                .findById(groupsPatchDTO.getGroupId())
                .orElseThrow(() -> new RuntimeException("Group Not Found"));

        // TODO: 변경 정보 있을 때만, 조회하도록 변경할 것
        // 변경할 부모 그룹을 조회
        var parentGroup = groupsRepository
                .findById(groupsPatchDTO.getNewParentGroupId())
                .orElseThrow(() -> new RuntimeException("ParentGroup Not Found"));

        // TODO: 테스트해 볼 것
        // 특정 인자가 null로 반환된 경우 수정정보가 저장되지 않는다.
        if(groupsPatchDTO.getNewGroupName() != null) group.setGroupsName(groupsPatchDTO.getNewGroupName());
        // parentGroupId는 정수형이므로, -1을 변경 정보 없음으로 인식한다.
        if(groupsPatchDTO.getNewParentGroupId() != -1) group.setParent(parentGroup);

        groupsRepository.save(group);

        return group;
    }
}
