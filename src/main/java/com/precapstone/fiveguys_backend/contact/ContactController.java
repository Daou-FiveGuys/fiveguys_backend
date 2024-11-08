package com.precapstone.fiveguys_backend.contact;

import com.precapstone.fiveguys_backend.common.CommonResponse;
import com.precapstone.fiveguys_backend.group.GroupPatchParm;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1/contact/")
@RequiredArgsConstructor
public class ContactController {
    private final ContactService contactService;

    // 주소록 조회 (특정 연락처 전체 연락처 존재)
    @GetMapping("groupName/{groupName}/name/{name}/telNum/{telNum}")
    public CommonResponse info(@PathVariable String groupName, @PathVariable String name, @PathVariable String telNum) {
        // ※ 전화번호 조회, 이름 조회 모두 가능해야 함
        Contact contact;
        if(name.equals("null")) contact = contactService.infoByGroupAndName(groupName, name);
        else contact = contactService.infoByGroupAndTelNum(groupName, telNum);

        return CommonResponse.builder().code(200).message("주소록 조회 성공").data(contact).build();
    }

    // 주소록 조회 (특정 연락처 전체 연락처 존재)
    @GetMapping("groupName/{groupName}")
    public CommonResponse info(@PathVariable String groupName) {
        // ※ 전화번호 조회, 이름 조회 모두 가능해야 함
        var contacts = contactService.contactsInGroup(groupName);
        return CommonResponse.builder().code(200).message("주소록 조회 성공").data(contacts).build();
    }
    
    /**
     * 특정 그룹에 주소록 생성
     * ※ 요청 사항에 그룹이 먼저 생성되어있어야 한다.
     */
    @PostMapping
    public CommonResponse create(@RequestBody ContactCreateParam contactCreateParam) {
        // 그룹 내부에 주소록 생성
        var contact = contactService.createContact(contactCreateParam);
        return CommonResponse.builder().code(200).message("주소록 생성 성공").data(contact).build();
    }

    // 주소록 삭제
    @DeleteMapping("{contactId}")
    public CommonResponse delete(@PathVariable int contactId) {
        // 그룹 내부에 주소록 삭제
        var contact = contactService.deleteContact(contactId);
        return CommonResponse.builder().code(200).message("주소록 삭제 성공").data(contact).build();
    }

    // 주소록 변경
    @PatchMapping
    public CommonResponse patch(@RequestBody ContactPatchParam contactPatchParam) {
        // 그룹 내부에 주소록 변경
        // 1. 위치 이동의 경우

        // 2. 정보 변경의 경우
        var contact = contactService.updateContact(contactPatchParam);

        return CommonResponse.builder().code(200).message("주소록 변경 성공").data(contact).build();
    }
}
