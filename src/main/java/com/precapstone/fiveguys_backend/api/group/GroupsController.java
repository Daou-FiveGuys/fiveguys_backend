package com.precapstone.fiveguys_backend.api.group;

import com.precapstone.fiveguys_backend.api.dto.groups.GroupsCreateDTO;
import com.precapstone.fiveguys_backend.api.dto.groups.GroupsPatchDTO;
import com.precapstone.fiveguys_backend.common.CommonResponse;
import com.precapstone.fiveguys_backend.common.auth.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/groups/")
@RequiredArgsConstructor
public class GroupsController {
    private final GroupService groupService;

    /**
     * 그룹 조회 - 그룹
     *
     * @param groupId 조회할 그룹ID
     * @return
     */
    @GetMapping("{groupId}")
    public ResponseEntity<CommonResponse> info(@PathVariable Long groupId) {
        // 그룹 조회
        var group = groupService.infoByGroupId(groupId);
        var groups = groupService.childGroupInfo(groupId);
        var response = GroupsResponse.builder().group(group).childGroups(groups).build();
        return ResponseEntity.ok(CommonResponse.builder().code(200).message("그룹 조회 성공").data(response).build());
    }

    /**
     * 그룹 생성
     * parentGroupId는 0일 때만 최상위 그룹으로 지정, 다른 존재하지 않는 Id의 경우 예외처리
     *
     * @param groupsCreateDTO 그룹명, 상위그룹ID
     * @return
     */
    @PostMapping
    public ResponseEntity<CommonResponse> create(@RequestBody GroupsCreateDTO groupsCreateDTO) {
        // 그룹 생성
        var groups = groupService.createGroup(groupsCreateDTO);
        return ResponseEntity.ok(CommonResponse.builder().code(200).message("그룹 생성 성공").data(groups).build());
    }

    /**
     * 그룹 삭제
     *
     * @param groupId 삭제할 그룹ID
     * @return
     */
    @DeleteMapping("{groupId}")
    public ResponseEntity<CommonResponse> delete(@PathVariable Long groupId) {
        // 그룹 삭제
        var group = groupService.deleteGroup(groupId);
        return ResponseEntity.ok(CommonResponse.builder().code(200).message("그룹 삭제 성공").data(group).build());
    }

    /**
     * 그룹 변경
     * ※ newParentId = -1 을 반환 시 변경하지 않음
     *
     * @param groupsPatchDTO 그룹ID, 그룹명, 상위 그룹 ID
     * @return
     */
    @PatchMapping
    public ResponseEntity<CommonResponse> update(@RequestBody GroupsPatchDTO groupsPatchDTO) {
        // 그룹 변경
        var group = groupService.updateGroup(groupsPatchDTO);
        return ResponseEntity.ok(CommonResponse.builder().code(200).message("그룹 변경 성공").data(group).build());
    }
}
