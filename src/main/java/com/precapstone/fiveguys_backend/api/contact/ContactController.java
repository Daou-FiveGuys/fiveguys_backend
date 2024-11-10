package com.precapstone.fiveguys_backend.api.contact;

import com.precapstone.fiveguys_backend.api.dto.contact.ContactCreateDTO;
import com.precapstone.fiveguys_backend.api.dto.contact.ContactPatchDTO;
import com.precapstone.fiveguys_backend.common.CommonResponse;
import com.precapstone.fiveguys_backend.entity.Contact;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/contact/")
@RequiredArgsConstructor
public class ContactController {
    private final ContactService contactService;

    /**
     * 주소록 조회 (사용자)
     * ※ 그룹 내 특정 연락처를 전달한다.
     *
     * @param groupsName 연락처가 소속된 그룹
     * @param nameOrTelNum 그룹에 저장된 명칭 혹은 연락처
     *
     * @return
     */
    @GetMapping("{groupsName}/{nameOrTelNum}")
    public ResponseEntity<CommonResponse> info(@PathVariable String groupsName, @PathVariable String nameOrTelNum) {
        // ※ 전화번호 조회, 이름 조회 모두 가능해야 함
        Contact contact;

        // 정수가 되는 문자열인 경우(연락처)
        if(isNumberic(nameOrTelNum)) contact = contactService.infoByGroupsAndTelNum(groupsName, nameOrTelNum);
        // 정수가 되지않는 문자열인 경우(그룹 내 명칭)
        else contact = contactService.infoByGroupAndName(groupsName, nameOrTelNum);

        return ResponseEntity.ok(CommonResponse.builder().code(200).message("주소록 조회 성공").data(contact).build());
    }

    /**
     * 주소록 조회 (전체)
     * ※ 그룹 내 전체 주소록을 전달한다.
     *
     * @param groupsName 조회할 그룹
     * @return
     */
    @GetMapping("{groupsName}")
    public ResponseEntity<CommonResponse> info(@PathVariable String groupsName) {
        // ※ 전화번호 조회, 이름 조회 모두 가능해야 함
        var contacts = contactService.contactsInGroup(groupsName);
        return ResponseEntity.ok(CommonResponse.builder().code(200).message("주소록 조회 성공").data(contacts).build());
    }

    /**
     * 특정 그룹에 주소록 생성
     * ※ 요청 사항에 그룹이 먼저 생성되어있어야 한다.
     *
     * @param contactCreateDTO 유저ID, 그룹ID, 그룹 내 명칭, 연락처
     * @return
     */
    @PostMapping
    public ResponseEntity<CommonResponse> create(@RequestBody ContactCreateDTO contactCreateDTO) {
        // 그룹 내부에 주소록 생성
        // TODO: 예외처리:: 같은 Group 내 name 및 telNum은 동일하면 안된다.
        var contact = contactService.createContact(contactCreateDTO);
        return ResponseEntity.ok(CommonResponse.builder().code(200).message("주소록 생성 성공").data(contact).build());
    }

    /**
     * 주소록 삭제
     * 그룹에 등록된 주소록을 삭제한다.
     *
     * @param contactId 삭제할 주소록ID
     * @return
     */
    @DeleteMapping("{contactId}")
    public ResponseEntity<CommonResponse> delete(@PathVariable ContactId contactId) {
        // 그룹 내부에 주소록 삭제
        var contact = contactService.deleteContact(contactId);
        return ResponseEntity.ok(CommonResponse.builder().code(200).message("주소록 삭제 성공").data(contact).build());
    }

    /**
     * 주소록 변경
     * 그룹에 등록된 주소록을 변경한다.
     * 
     * @param contactPatchDTO 유저ID, 그룹ID, 그룹 내 명칭, 연락처
     * @return
     */
    @PatchMapping
    public ResponseEntity<CommonResponse> update(@RequestBody ContactPatchDTO contactPatchDTO) {
        // 그룹 내부에 주소록 변경
        var contact = contactService.updateContact(contactPatchDTO);

        return ResponseEntity.ok(CommonResponse.builder().code(200).message("주소록 변경 성공").data(contact).build());
    }

    /**
     * 변경된 인자가 정수로만 되어있는 문자열인지 판단하는 함수
     * ※ 연락처의 경우 문자열을 정수로도 표현할 수 있음을 통해, 함수를 분기 
     *
     * @param str 주소록 별명 정보 or 주소록 연락처 정보
     * @return 주소록 연락처 정보가 전달된 경우 true
     */
    private static boolean isNumberic(String str) {
        return str.chars().allMatch(Character::isDigit);
    }
}
