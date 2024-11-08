package com.precapstone.fiveguys_backend.group;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupsRepository extends JpaRepository<Groups, Integer> {
    // 그룹 이름은 단일 개체이다.
    Optional<Groups> findByGroupsName(String groupName);

    // 상위 그룹은 단일 그룹임
    Optional<Groups> findByParent(Groups parentGroups);
    
    // 하위 그룹에 여러 그룹이 존재할 수 있음
    Optional<List<Groups>> findByChild(Groups childGroups);
}
