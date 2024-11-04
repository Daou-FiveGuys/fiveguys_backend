package com.precapstone.fiveguys_backend.api.member;

import com.precapstone.fiveguys_backend.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findByUserId(String userId);
}
