package com.precapstone.fiveguys_backend.api.group;

import com.precapstone.fiveguys_backend.entity.Groups;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupsRepository extends JpaRepository<Groups, Long> {
    /** 그룹명을 통해 그룹 엔티티를 조회하는 함수 / 그룹 이름은 단일 개체이다. */
    Optional<Groups> findByGroupsName(String groupName);

    /** 하위 그룹을 조회하는 함수이다. ※ 테이블에서 parentId에 본인을 명시해둔 엔티티를 조회한다. */
    Optional<List<Groups>> findByParent(Groups parentGroups);
}
