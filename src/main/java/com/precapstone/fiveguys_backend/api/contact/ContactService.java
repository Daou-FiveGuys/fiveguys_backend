package com.precapstone.fiveguys_backend.api.contact;

import com.precapstone.fiveguys_backend.api.auth.JwtTokenProvider;
import com.precapstone.fiveguys_backend.api.dto.contact.ContactCreateDTO;
import com.precapstone.fiveguys_backend.api.dto.contact.ContactPatchDTO;
import com.precapstone.fiveguys_backend.api.group.GroupService;
import com.precapstone.fiveguys_backend.api.user.UserService;
import com.precapstone.fiveguys_backend.entity.Contact;
import com.precapstone.fiveguys_backend.exception.ControlledException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.precapstone.fiveguys_backend.exception.errorcode.BasicErrorCode.ACCESS_DENIED;
import static com.precapstone.fiveguys_backend.exception.errorcode.ContactErrorCode.*;
import static com.precapstone.fiveguys_backend.exception.errorcode.UserErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ContactService {
    private final GroupService groupService;
    private final ContactRepository contactRepository;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 주소록을 생성하는 함수
     * TODO: 데이터베이스 문자 인코딩 방식으로 인해, 한글 name이 작성이 안됨
     *
     * @param contactCreateDTO 주소록 생성 정보
     * @return
     */
    public Contact createContact(ContactCreateDTO contactCreateDTO, String accessToken) {
        // 그룹ID를 통해 추가할 그룹의 정보를 조회한다.
        var groups = groupService.infoByGroupId(contactCreateDTO.getGroupId());
        var user =  userService.findByUserId(jwtTokenProvider.getUserIdFromToken(accessToken))
                .orElseThrow(() -> new ControlledException(USER_NOT_FOUND));

        // [예외처리] 올바르지 않은 연락처 서식
        if(!isValidPhoneNumber(contactCreateDTO.getTelNum())) throw new ControlledException(INVALID_FORMAT);

        // [예외처리] 올바르지 않은 그룹명 요청
        // 동일 그룹 내에 같은 이름의 연락처가 존재하는 경우
        if(contactRepository.findByGroupsAndName(groups, contactCreateDTO.getName()).orElse(null) != null)
            throw new ControlledException(CONTACT_NAME_ALREADY_EXISTS);

        // [예외처리] 올바르지 않은 전화번호 요청
        // 동일 그룹 내에 같은 전화번호의 연락처가 존재하는 경우
        if(contactRepository.findByGroupsAndTelNum(groups, contactCreateDTO.getTelNum()).orElse(null) != null)
            throw new ControlledException(CONTACT_TELNUM_ALREADY_EXISTS);

        // 주소록 생성
        var contact = Contact.builder()
                .user(user)
                .groups(groups)
                .name(contactCreateDTO.getName())
                .telNum(contactCreateDTO.getTelNum())
                .build();

        // 주소록 저장
        contactRepository.save(contact);

        return contact;
    }

    /**
     * 그룹 명과 그룹 내 명칭을 통해, 특정 주소록의 정보를 조회하는 함수
     *
     * @param groupName 등록된 그룹의 이름
     * @param name 조회할 그룹 내 명칭
     * @return
     */
    public Contact infoByGroupAndName(String groupName, String name, String accessToken) {
        // 그룹명을 통해 그룹 정보를 조회한다.
        var group = groupService.infoByName(groupName);

        // 그룹과 그룹 내 명칭을 통해 주소록을 조회한다.
        var contact = contactRepository.findByGroupsAndName(group, name)
                .orElseThrow(() -> new ControlledException(CONTACT_NOT_FOUND));

        // [예외처리] 권한 소유자만 데이터를 반환 받을 수 있다.
        if(contact.getUser().getUserId().equals(jwtTokenProvider.getUserIdFromToken(accessToken)))
            throw new ControlledException(ACCESS_DENIED);

        return contact;
    }

    /**
     * 그룹 명과 연락처를 통해, 특정 주소록의 정보를 조회하는 함수
     *
     * @param groupName   등록된 그룹의 이름
     * @param telNum      조회할 연락처
     * @param accessToken
     * @return
     */
    public Contact infoByGroupsAndTelNum(String groupName, String telNum, String accessToken) {
        // 그룹명을 통해 그룹 정보를 조회한다.
        var group = groupService.infoByName(groupName);

        // 그룹과 연락처를 통해 주소록을 조회한다.
        var contact = contactRepository.findByGroupsAndTelNum(group, telNum)
                .orElseThrow(() -> new ControlledException(CONTACT_NOT_FOUND));

        // [예외처리] 권한 소유자만 데이터를 반환 받을 수 있다.
        if(contact.getUser().getUserId().equals(jwtTokenProvider.getUserIdFromToken(accessToken)))
            throw new ControlledException(ACCESS_DENIED);

        return contact;
    }

    /**
     * 그룹명을 통해, 그룹 내 모든 주소록을 조회한다.
     *
     * @param groupName   조회할 그룹명
     * @param accessToken
     * @return
     */
    public List<Contact> contactsInGroup(String groupName, String accessToken) {
        var group = groupService.infoByName(groupName);
        var contacts = contactRepository.findByGroups(group)
                .orElseThrow(() -> new ControlledException(CONTACT_NOT_FOUND));

        // [예외처리] 권한 소유자만 데이터를 반환 받을 수 있다.
        var userId = jwtTokenProvider.getUserIdFromToken(accessToken);
        for(var contact : contacts)
            if(contact.getUser().getUserId().equals(userId))
                throw new ControlledException(ACCESS_DENIED);

        return contacts;
    }

    /**
     * 주소록을 삭제하는 함수
     *
     * @param contactId   삭제할 연락처ID
     * @param accessToken
     * @return
     */
    public Contact deleteContact(Long contactId, String accessToken) {
        var contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new ControlledException(CONTACT_NOT_FOUND));

        // [예외처리] 권한 소유자만 데이터를 반환 받을 수 있다.
        if(contact.getUser().getUserId().equals(jwtTokenProvider.getUserIdFromToken(accessToken)))
            throw new ControlledException(ACCESS_DENIED);

        contactRepository.deleteByContactId(contact.getContactId());

        return contact;
    }

    /**
     * 주소록을 수정하는 함수
     * TODO: 연락처 수정을 위한 추가적인 키 필요함
     *
     * @param contactPatchDTO 주소록 변경 정보
     * @param accessToken
     * @return
     */
    public Contact updateContact(ContactPatchDTO contactPatchDTO, String accessToken) {
        var contact = contactRepository.findById(contactPatchDTO.getContactId())
                .orElseThrow(() -> new ControlledException(CONTACT_NOT_FOUND));

        // [예외처리] 권한 소유자만 데이터를 반환 받을 수 있다.
        if(contact.getUser().getUserId().equals(jwtTokenProvider.getUserIdFromToken(accessToken)))
            throw new ControlledException(ACCESS_DENIED);

        // [예외처리] 올바르지 않은 연락처 서식
        if(!isValidPhoneNumber(contactPatchDTO.getNewTelNum())) throw new ControlledException(INVALID_FORMAT);

        // 특정 인자가 null로 반환된 경우 수정정보가 저장되지 않는다.
        if(contactPatchDTO.getNewName() != null) contact.setName(contactPatchDTO.getNewName());
        if(contactPatchDTO.getNewTelNum() != null) contact.setTelNum(contactPatchDTO.getNewTelNum());

        if(contactPatchDTO.getNewGroupId() != -1) {
            var group = groupService.infoByGroupId(contactPatchDTO.getNewGroupId());
            contact.setGroups(group);
        }

        // 주소록 수정
        contactRepository.save(contact);

        return contact;
    }

    /**
     * 전화번호 서식 검사
     *
     * @param phoneNumber 연락처가 작성된 문자열
     * @return 11자리 숫자 문자열인 경우 true
     */
    private boolean isValidPhoneNumber(String phoneNumber) {
        // 전화번호가 11자리인지 확인하고 모든 문자가 숫자인지 검사
        return phoneNumber.length() == 11 && phoneNumber.matches("\\d+");
    }
}
