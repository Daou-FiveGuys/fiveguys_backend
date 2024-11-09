package com.precapstone.fiveguys_backend.api.dto;

import com.precapstone.fiveguys_backend.api.contact.ContactId;
import lombok.Builder;
import lombok.Getter;

/**
 * 주소록에 연락처를 등록하기 위한 전달 인자
 * TODO: CreateDTD와 PatchDTO 병합 예정
 */
@Builder
@Getter
public class ContactCreateDTO {
    private ContactId contactId; // 주소록ID:: 주소록을 등록할 그룹ID와 유저ID
    private String name; // 그룹 내 명칭
    private String telNum; // 연락처
}
