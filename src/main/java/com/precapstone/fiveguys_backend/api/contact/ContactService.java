package com.precapstone.fiveguys_backend.api.contact;

import com.precapstone.fiveguys_backend.api.dto.contact.ContactCreateDTO;
import com.precapstone.fiveguys_backend.api.dto.contact.ContactPatchDTO;
import com.precapstone.fiveguys_backend.api.group.GroupService;
import com.precapstone.fiveguys_backend.api.user.UserService;
import com.precapstone.fiveguys_backend.entity.Contact;
import com.precapstone.fiveguys_backend.exception.ControlledException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.precapstone.fiveguys_backend.exception.errorcode.ContactErrorCode.*;

@Service
@RequiredArgsConstructor
public class ContactService {
    private final GroupService groupService;
    private final ContactRepository contactRepository;
    private final UserService userService;

    /**
     * 주소록을 생성하는 함수
     * TODO: 데이터베이스 문자 인코딩 방식으로 인해, 한글 name이 작성이 안됨
     *
     * @param contactCreateDTO 주소록 생성 정보
     * @return
     */
    public Contact createContact(ContactCreateDTO contactCreateDTO) {
        // 그룹ID를 통해 추가할 그룹의 정보를 조회한다.
        var groups = groupService.infoByGroupId(contactCreateDTO.getContactId().getGroupsId());
        var user = userService.findById(contactCreateDTO.getContactId().getUserId());

        // [예외처리] 올바르지 않은 그룹명 요청
        // 동일 그룹 내에 같은 이름의 연락처가 존재하는 경우
        if(contactRepository.findByGroupsAndName(groups, contactCreateDTO.getName()).orElse(null) != null)
            throw new ControlledException(CONTACT_NAME_ALREADY_EXISTS);

        // [예외처리] 올바르지 않은 전화번호 요청
        // 동일 그룹 내에 같은 전화번호의 연락처가 존재하는 경우
        if(contactRepository.findByGroupsAndTelNum(groups, contactCreateDTO.getTelNum()).orElse(null) != null)
            throw new ControlledException(CONTACT_TELNUM_ALREADY_EXISTS);

        var contactId = ContactId.builder().groupsId(groups.getGroupsId()).userId(user.getId()).build();

        // 주소록 생성
        var contact = Contact.builder()
                .contactId(contactId)
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
    public Contact infoByGroupAndName(String groupName, String name) {
        // 그룹명을 통해 그룹 정보를 조회한다.
        var group = groupService.infoByName(groupName);

        // 그룹과 그룹 내 명칭을 통해 주소록을 조회한다.
        var contact = contactRepository.findByGroupsAndName(group, name)
                .orElseThrow(() -> new ControlledException(CONTACT_NOT_FOUND));

        return contact;
    }

    /**
     * 그룹 명과 연락처를 통해, 특정 주소록의 정보를 조회하는 함수
     *
     * @param groupName 등록된 그룹의 이름
     * @param telNum 조회할 연락처
     * @return
     */
    public Contact infoByGroupsAndTelNum(String groupName, String telNum) {
        // 그룹명을 통해 그룹 정보를 조회한다.
        var group = groupService.infoByName(groupName);

        // 그룹과 연락처를 통해 주소록을 조회한다.
        var contact = contactRepository.findByGroupsAndTelNum(group, telNum)
                .orElseThrow(() -> new ControlledException(CONTACT_NOT_FOUND));

        return contact;
    }

    /**
     * 그룹명을 통해, 그룹 내 모든 주소록을 조회한다.
     *
     * @param groupName 조회할 그룹명
     * @return
     */
    public List<Contact> contactsInGroup(String groupName) {
        var group = groupService.infoByName(groupName);
        var contacts = contactRepository.findByGroups(group)
                .orElseThrow(() -> new ControlledException(CONTACT_NOT_FOUND));

        return contacts;
    }

    /**
     * 주소록을 삭제하는 함수
     *
     * @param contactId 삭제할 주소록ID(GroupId, UserId)
     * @return
     */
    public Contact deleteContact(ContactId contactId) {
        var contact = contactRepository.findByContactId(contactId)
                .orElseThrow(() -> new ControlledException(CONTACT_NOT_FOUND));

        contactRepository.deleteByContactId(contact.getContactId());

        return contact;
    }

    /**
     * 주소록을 수정하는 함수
     * TODO: 연락처 수정을 위한 추가적인 키 필요함
     *
     * @param contactPatchDTO 주소록 변경 정보
     * @return
     */
    public Contact updateContact(ContactPatchDTO contactPatchDTO) {
        var contact = contactRepository.findByContactId(contactPatchDTO.getContactId())
                .orElseThrow(() -> new ControlledException(CONTACT_NOT_FOUND));

        // 특정 인자가 null로 반환된 경우 수정정보가 저장되지 않는다.
        if(contactPatchDTO.getNewName() != null) contact.setName(contactPatchDTO.getNewName());
        if(contactPatchDTO.getNewTelNum() != null) contact.setTelNum(contactPatchDTO.getNewTelNum());
        // TODO: 테스트 필요
        if(contactPatchDTO.getNewGroupId() != -1) {
            var group = groupService.infoByGroupId(contactPatchDTO.getNewGroupId());
            contact.setGroups(group);
        }

        // 주소록 수정
        contactRepository.save(contact);

        return contact;
    }
}
