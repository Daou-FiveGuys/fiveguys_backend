package com.precapstone.fiveguys_backend.contact;

import com.precapstone.fiveguys_backend.entity.User;
import com.precapstone.fiveguys_backend.group.Groups;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    /** 특정 주소록 하나만 조회하는 함수 */
    Optional<Contact> findByContactId(ContactId contactId);

    /** 특정 사용자가 소유한 모든 연락처를 조회하는 함수 */
    Optional<List<Contact>> findByUser(User user);

    /** 특정 그룹의 주소록 정보를 조회하는 함수 */
    Optional<List<Contact>> findByGroups(Groups groups);

    /** 특정 그룹에서 그룹 내 명칭으로 조회하는 함수 */
    Optional<Contact> findByGroupsAndName(Groups groups, String name);

    /** 특정 그룹에서 연락처 정보로 조회하는 함수 */
    Optional<Contact> findByGroupsAndTelNum(Groups groups, String telNum);

    // 다른 그룹 내부에 동일한 이름과 연락처가 존재할 수 있음
    Optional<List<Contact>> findByName(String name);
    Optional<List<Contact>> findByTelNum(String telNum);

    /** 특정 주소록 하나를 삭제하는 함수 */
    Optional<Contact> deleteByContactId(ContactId contactId);
}
