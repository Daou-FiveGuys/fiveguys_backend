package com.precapstone.fiveguys_backend.group;

import com.precapstone.fiveguys_backend.common.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/groups/")
@RequiredArgsConstructor
public class GroupsController {
    private final GroupService groupService;

    // 그룹 조회

    /**
     * 그룹 조회
     *
     * @param groupId 조회할 그룹ID
     * @return
     */
    @GetMapping("{groupId}")
    public CommonResponse info(@PathVariable Long groupId) {
        var groups = groupService.childGroupInfo(groupId);
        return CommonResponse.builder().code(200).message("그룹 조회 성공").data(groups).build();
    }

    /**
     * 그룹 생성
     * parentGroupId는 0일 때만 최상위 그룹으로 지정, 다른 존재하지 않는 Id의 경우 예외처리
     * TODO: 예외처리 할 것
     * 
     * @param groupsCreateDTO 그룹명, 상위그룹ID
     * @return
     */
    @PostMapping
    public CommonResponse create(@RequestBody GroupsCreateDTO groupsCreateDTO) {
        var group = groupService.createGroup(groupsCreateDTO);
        return CommonResponse.builder().code(200).message("그룹 생성 성공").data(group).build();
    }

    /**
     * 그룹 삭제
     *
     * @param groupId 삭제할 그룹ID
     * @return
     */
    @DeleteMapping("{groupId}")
    public CommonResponse delete(@PathVariable Long groupId) {
        // 그룹 삭제
        var group = groupService.deleteGroup(groupId);
        return CommonResponse.builder().code(200).message("그룹 삭제 성공").data(group).build();
    }

    /**
     * 그룹 변경
     * ※ newParentId = -1 을 반환 시 변경하지 않음
     *
     * @param groupsPatchDTO 그룹ID, 그룹명, 상위 그룹 ID
     * @return
     */
    @PatchMapping
    public CommonResponse update(@RequestBody GroupsPatchDTO groupsPatchDTO) {
        // 그룹 변경
        // 1. 위치 이동의 경우
        // TODO: 새로운 그룹을 조회한 후, parent를 변경한다.

        // 2. 정보 변경의 경우
        var group = groupService.updateGroup(groupsPatchDTO);
        return CommonResponse.builder().code(200).message("그룹 변경 성공").data(group).build();
    }
}
