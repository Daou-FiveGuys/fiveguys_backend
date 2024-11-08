package com.precapstone.fiveguys_backend.group;

import com.precapstone.fiveguys_backend.common.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/groups/")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;

    // 그룹 조회
    @GetMapping("{groupId}")
    public CommonResponse info(@PathVariable int groupId) {
        // 그룹 조회
        var groups = groupService.childGroupInfo(groupId);
        return CommonResponse.builder().code(200).message("그룹 조회 성공").data(groups).build();
    }

    // 그룹 생성
    /**
     * parentGroupId는 0일 때만 최상위 그룹으로 지정, 다른 존재하지 않는 Id의 경우 예외처리
     * 
     * TODO: 반환 시 Parent와 ChildGroup 사이에서 무한루프 발생.
     * 
     * @param groupCreateParm
     * @return
     */
    @PostMapping
    public CommonResponse create(@RequestBody GroupCreateParm groupCreateParm) {
        // 그룹 생성
        var group = groupService.createGroup(groupCreateParm);
        return CommonResponse.builder().code(200).message("그룹 생성 성공").data(group).build();
    }

    // 그룹 삭제
    @DeleteMapping("{groupId}")
    public CommonResponse delete(@PathVariable int groupId) {
        // 그룹 삭제
        var group = groupService.deleteGroup(groupId);
        return CommonResponse.builder().code(200).message("그룹 삭제 성공").data(group).build();
    }

    // 그룹 변경

    /**
     * newParentId = -1 을 반환 시 변경하지 않음
     * @param groupPatchParm
     * @return
     */
    @PatchMapping
    public CommonResponse update(@RequestBody GroupPatchParm groupPatchParm) {
        // 그룹 변경
        // 1. 위치 이동의 경우
        // 2. 정보 변경의 경우
        var group = groupService.updateGroup(groupPatchParm);
        return CommonResponse.builder().code(200).message("그룹 변경 성공").data(group).build();
    }
}
