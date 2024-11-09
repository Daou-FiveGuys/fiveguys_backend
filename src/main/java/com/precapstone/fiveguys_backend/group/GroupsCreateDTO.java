package com.precapstone.fiveguys_backend.group;

import lombok.Builder;
import lombok.Getter;

/**
 * 그룹을 생성하기 위한 전달 인자
 * TODO: CreateDTD와 PatchDTO 병합 예정
 */
@Builder
@Getter
public class GroupsCreateDTO {
    private String groupName; // 그룹명
    private Long parentGroupId; // 상위 그룹ID, ※ 0인 경우 최상위 그룹이다.
}
