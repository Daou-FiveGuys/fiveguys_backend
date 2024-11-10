package com.precapstone.fiveguys_backend.api.group;

import com.precapstone.fiveguys_backend.api.dto.groups.GroupsCreateDTO;
import com.precapstone.fiveguys_backend.api.dto.groups.GroupsPatchDTO;
import com.precapstone.fiveguys_backend.entity.Groups;
import com.precapstone.fiveguys_backend.exception.ControlledException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.precapstone.fiveguys_backend.exception.errorcode.GroupsErrorCode.*;

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

        /*
         * [예외처리] 올바르지 않는 부모 그룹 요청
         * 최상위 그룹(parentGroupId가 0인 경우)을 제외한 null 값의 경우 잘못된 요청
         */
        if(parentGroup == null && groupsCreateDTO.getParentGroupId() != 0)
            throw new ControlledException(PARENT_GROUP_NOT_FOUND);

        /*
         * [예외처리] 올바르지 않은 그룹명 요청
         * 그룹 이름이 이미 존재하는 경우. 잘못된 요청
         * ※ 그룹 소유자에 대한 열이 존재하지 않기 때문에, 그룹명을 Unique하다.
         */
        var groupName = groupsRepository.findByGroupsName(groupsCreateDTO.getGroupName()).orElse(null);
        if(groupName != null)
            throw new ControlledException(GROUP_ALREADY_EXISTS);

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
                .orElseThrow(() -> new ControlledException(GROUP_NOT_FOUND));
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
                .orElseThrow(() -> new ControlledException(GROUP_NOT_FOUND));

        return group;
    }

    /**
     * 그룹ID를 통해 본인의 하위 그룹을 조회한다.
     *
     * @param groupId
     * @return
     */
    public List<Groups> childGroupInfo(Long groupId) {
        var group = groupsRepository.findById(groupId)
                .orElseThrow(() -> new ControlledException(GROUP_NOT_FOUND));

        var groups = groupsRepository.findByParent(group)
                .orElseThrow(() -> new ControlledException(GROUP_NOT_FOUND));

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
                .orElseThrow(() -> new ControlledException(GROUP_NOT_FOUND));

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
                .orElseThrow(() -> new ControlledException(GROUP_NOT_FOUND));

        // 특정 인자가 null로 반환된 경우 수정정보가 저장되지 않는다.
        if(groupsPatchDTO.getNewGroupName() != null) group.setGroupsName(groupsPatchDTO.getNewGroupName());

        // parentGroupId는 정수형이므로, -1을 변경 정보 없음으로 인식한다.
        if(groupsPatchDTO.getNewParentGroupId() != -1) {
            // 변경할 부모 그룹을 조회
            var parentGroup = groupsRepository
                    .findById(groupsPatchDTO.getNewParentGroupId())
                    .orElseThrow(() -> new ControlledException(PARENT_GROUP_NOT_FOUND));

            group.setParent(parentGroup);
        }

        // 그룹 수정
        groupsRepository.save(group);

        return group;
    }
}
