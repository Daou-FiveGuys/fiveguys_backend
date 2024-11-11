package com.precapstone.fiveguys_backend.api.contact;

import com.precapstone.fiveguys_backend.api.dto.contact.ContactCreateDTO;
import com.precapstone.fiveguys_backend.api.dto.contact.ContactPatchDTO;
import com.precapstone.fiveguys_backend.api.group.GroupService;
import com.precapstone.fiveguys_backend.common.CommonResponse;
import com.precapstone.fiveguys_backend.common.auth.JwtFilter;
import com.precapstone.fiveguys_backend.entity.Contact;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/contact/")
@RequiredArgsConstructor
public class ContactController {
    private final ContactService contactService;
    private final GroupService groupService;

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
    public ResponseEntity<CommonResponse> info(@PathVariable String groupsName, @PathVariable String nameOrTelNum, @RequestHeader("Authorization") String authorization) {
        String accessToken = authorization.replace(JwtFilter.TOKEN_PREFIX, "");

        // ※ 전화번호 조회, 이름 조회 모두 가능해야 함
        Contact contact;

        // 정수가 되는 문자열인 경우(연락처)
        if(isNumberic(nameOrTelNum)) contact = contactService.infoByGroupsAndTelNum(groupsName, nameOrTelNum, accessToken);
        // 정수가 되지않는 문자열인 경우(그룹 내 명칭)
        else contact = contactService.infoByGroupAndName(groupsName, nameOrTelNum, accessToken);

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
    public ResponseEntity<CommonResponse> info(@PathVariable String groupsName, @RequestHeader("Authorization") String authorization) {
        String accessToken = authorization.replace(JwtFilter.TOKEN_PREFIX, "");

        // ※ 전화번호 조회, 이름 조회 모두 가능해야 함
        var contacts = contactService.contactsInGroup(groupsName, accessToken);
        var groups = groupService.childGroupInfo(groupsName);
        var response = new ContactResponse(contacts, groups);
        return ResponseEntity.ok(CommonResponse.builder().code(200).message("주소록 조회 성공").data(response).build());
    }

    /**
     * 특정 그룹에 주소록 생성
     * ※ 요청 사항에 그룹이 먼저 생성되어있어야 한다.
     *
     * @param contactCreateDTO 유저ID, 그룹ID, 그룹 내 명칭, 연락처
     * @param authorization 연락처를 소유할 유저ID를 얻기위한 인증키
     * @return
     */
    @PostMapping
    public ResponseEntity<CommonResponse> create(@RequestBody ContactCreateDTO contactCreateDTO, @RequestHeader("Authorization") String authorization) {
        String accessToken = authorization.replace(JwtFilter.TOKEN_PREFIX, "");

        // 그룹 내부에 주소록 생성
        var contact = contactService.createContact(contactCreateDTO, accessToken);
        return ResponseEntity.ok(CommonResponse.builder().code(200).message("주소록 생성 성공").data(contact).build());
    }

    /**
     * 주소록 삭제
     * 그룹에 등록된 주소록을 삭제한다.
     *
     * @param contactId 삭제할 연락처ID
     * @return
     */
    @Transactional
    @DeleteMapping("{contactId}")
    public ResponseEntity<CommonResponse> delete(@PathVariable Long contactId, @RequestHeader("Authorization") String authorization) {
        String accessToken = authorization.replace(JwtFilter.TOKEN_PREFIX, "");

        // 그룹 내부에 주소록 삭제
        var contact = contactService.deleteContact(contactId, accessToken);
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
    public ResponseEntity<CommonResponse> update(@RequestBody ContactPatchDTO contactPatchDTO, @RequestHeader("Authorization") String authorization) {
        String accessToken = authorization.replace(JwtFilter.TOKEN_PREFIX, "");

        // 그룹 내부에 주소록 변경
        var contact = contactService.updateContact(contactPatchDTO, accessToken);

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
