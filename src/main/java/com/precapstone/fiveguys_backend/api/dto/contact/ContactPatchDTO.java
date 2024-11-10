package com.precapstone.fiveguys_backend.api.dto.contact;

import com.precapstone.fiveguys_backend.api.contact.ContactId;
import lombok.Builder;
import lombok.Getter;

/**
 * 주소록에 등록된 연락처를 변경하기 위한 전달 인자
 * TODO: CreateDTD와 PatchDTO 병합 예정
 */
@Builder
@Getter
public class ContactPatchDTO {
    private ContactId contactId; // 변경할 주소록ID
    private String newName; // 변경할 그룹 내 명칭
    private String newTelNum; // 변경할 연락처
    private Long newGroupId; // 변경할 부모 그룹
}
