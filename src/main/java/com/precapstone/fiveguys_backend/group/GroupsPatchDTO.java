package com.precapstone.fiveguys_backend.group;

import lombok.Builder;
import lombok.Getter;

/**
 * 그룹을 변경하기 위한 전달 인자
 * TODO: CreateDTD와 PatchDTO 병합 예정
 */
@Builder
@Getter
public class GroupsPatchDTO {
    private Long groupId; // 변경할 그룹ID
    private String newGroupName; // 변경할 그룹명
    private Long newParentGroupId; // 이동할 상위 그룹ID
}
